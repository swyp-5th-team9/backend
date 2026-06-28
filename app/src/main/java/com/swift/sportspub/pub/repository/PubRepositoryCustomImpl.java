package com.swift.sportspub.pub.repository;

import com.swift.sportspub.pub.dto.PubListSearchCondition;
import com.swift.sportspub.pub.entity.Region;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;

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
        if (condition.capacityRange() != null) {
            where.append(" AND p.capacity_range = :capacityRange ");
            params.put("capacityRange", condition.capacityRange().name());
        }
        if (condition.teamId() != null) {
            where.append(" AND EXISTS (SELECT 1 FROM pub_supported_teams pst ")
                 .append(" WHERE pst.pub_id = p.pub_id AND pst.team_id = :teamId) ");
            params.put("teamId", condition.teamId());
        }
        appendCodeAndFilter(where, params, "pub_facilities", "facility_code",
                "facilityCodes", condition.facilityCodes());
        appendCodeAndFilter(where, params, "pub_styles", "style_code",
                "styleCodes", condition.styleCodes());
        appendCodeAndFilter(where, params, "pub_themes", "theme_code",
                "themeCodes", condition.themeCodes());
        appendCodeAndFilter(where, params, "pub_food_tags", "food_code",
                "foodCodes", condition.foodCodes());

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
}
