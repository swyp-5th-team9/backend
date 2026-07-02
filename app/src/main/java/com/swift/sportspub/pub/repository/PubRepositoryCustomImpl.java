package com.swift.sportspub.pub.repository;

import com.swift.sportspub.pub.dto.BusinessDayFilter;
import com.swift.sportspub.pub.dto.PubListSearchCondition;
import com.swift.sportspub.pub.entity.Region;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class PubRepositoryCustomImpl implements PubRepositoryCustom {

    private final EntityManager entityManager;

    @Override
    public PubIdPage searchPubIds(PubListSearchCondition condition) {
        StringBuilder where = new StringBuilder(" WHERE p.deleted_at IS NULL ");
        Map<String, Object> params = new HashMap<>();

        if (condition.keyword() != null && !condition.keyword().isBlank()) {
            where.append(" AND (p.name ILIKE :keyword OR p.address ILIKE :keyword) ");
            params.put("keyword", "%" + condition.keyword().trim() + "%");
        }
        if (!condition.regions().isEmpty()) {
            where.append(" AND p.region IN (:regions) ");
            params.put("regions", condition.regions().stream().map(Region::name).toList());
        }
        if (condition.subRegion() != null) {
            where.append(" AND p.sub_region = :subRegion ");
            params.put("subRegion", condition.subRegion().name());
        }
        if (condition.capacityRange() != null) {
            where.append(" AND p.capacity_range = :capacityRange ");
            params.put("capacityRange", condition.capacityRange().name());
        }
        if (!condition.teamIds().isEmpty()) {
            where.append(" AND EXISTS (SELECT 1 FROM pub_supported_teams pst ")
                 .append(" WHERE pst.pub_id = p.pub_id AND pst.team_id IN (:teamIds)) ");
            params.put("teamIds", condition.teamIds());
        }
        appendCodeAndFilter(where, params, "pub_facilities", "facility_code",
                "facilityCodes", condition.facilityCodes());
        appendCodeAndFilter(where, params, "pub_styles", "style_code",
                "styleCodes", condition.styleCodes());
        appendCodeAndFilter(where, params, "pub_themes", "theme_code",
                "themeCodes", condition.themeCodes());
        appendCodeAndFilter(where, params, "pub_food_tags", "food_code",
                "foodCodes", condition.foodCodes());
        appendBusinessDayFilter(where, params, condition.businessDay());
        appendOpenNowFilter(where, params, condition.openNow());

        String selectSql = "SELECT p.pub_id FROM pubs p" + where
                + " ORDER BY p.favorite_count DESC, p.pub_id DESC"
                + " LIMIT :size OFFSET :offset ";
        String countSql = "SELECT COUNT(*) FROM pubs p" + where;

        Query selectQuery = entityManager.createNativeQuery(selectSql);
        Query countQuery = entityManager.createNativeQuery(countSql);
        params.forEach((k, v) -> {
            selectQuery.setParameter(k, v);
            countQuery.setParameter(k, v);
        });
        selectQuery.setParameter("size", condition.size());
        selectQuery.setParameter("offset", (long) condition.page() * condition.size());

        @SuppressWarnings("unchecked")
        List<Number> rawIds = selectQuery.getResultList();
        List<Long> pubIds = new ArrayList<>(rawIds.size());
        for (Number n : rawIds) {
            pubIds.add(n.longValue());
        }
        long total = ((Number) countQuery.getSingleResult()).longValue();
        return new PubIdPage(pubIds, total);
    }

    private void appendCodeAndFilter(StringBuilder where, Map<String, Object> params,
                                     String table, String column,
                                     String paramName, List<String> codes) {
        if (codes == null || codes.isEmpty()) {
            return;
        }
        where.append(" AND EXISTS (SELECT 1 FROM ").append(table).append(" c ")
             .append(" WHERE c.pub_id = p.pub_id AND c.").append(column)
             .append(" IN (:").append(paramName).append(") ")
             .append(" GROUP BY c.pub_id HAVING COUNT(DISTINCT c.").append(column)
             .append(") = :").append(paramName).append("Size) ");
        params.put(paramName, codes);
        params.put(paramName + "Size", (long) codes.size());
    }

    private void appendBusinessDayFilter(StringBuilder where, Map<String, Object> params,
                                         BusinessDayFilter businessDay) {
        if (businessDay == null) {
            return;
        }
        where.append(" AND EXISTS (SELECT 1 FROM pub_business_hours h ")
             .append(" WHERE h.pub_id = p.pub_id ")
             .append(" AND h.day_of_week IN (:businessDays) ")
             .append(" AND h.is_closed = FALSE ")
             .append(" GROUP BY h.pub_id ")
             .append(" HAVING COUNT(DISTINCT h.day_of_week) = :businessDaysSize) ");
        params.put("businessDays", businessDay.getDays());
        params.put("businessDaysSize", (long) businessDay.getDays().size());
    }

    private void appendOpenNowFilter(StringBuilder where, Map<String, Object> params,
                                     Boolean openNow) {
        if (openNow == null || !openNow) {
            return;
        }
        LocalDateTime now = LocalDateTime.now();
        int nowDay = now.getDayOfWeek().getValue();
        int prevDay = nowDay == 1 ? 7 : nowDay - 1;
        where.append(" AND EXISTS (SELECT 1 FROM pub_business_hours h ")
             .append(" WHERE h.pub_id = p.pub_id ")
             .append(" AND h.is_closed = FALSE AND h.open_time IS NOT NULL AND h.close_time IS NOT NULL ")
             .append(" AND ( ")
             .append("   (h.day_of_week = :nowDay AND h.open_time <= h.close_time ")
             .append("      AND CAST(:nowTime AS TIME) BETWEEN h.open_time AND h.close_time) ")
             .append("   OR (h.day_of_week = :nowDay AND h.open_time > h.close_time ")
             .append("      AND CAST(:nowTime AS TIME) >= h.open_time) ")
             .append("   OR (h.day_of_week = :prevDay AND h.open_time > h.close_time ")
             .append("      AND CAST(:nowTime AS TIME) < h.close_time) ")
             .append(" )) ");
        params.put("nowDay", nowDay);
        params.put("prevDay", prevDay);
        params.put("nowTime", now.toLocalTime().toString());
    }
}
