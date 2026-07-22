package com.swift.sportspub.pub.service;

import com.swift.sportspub.common.exception.BusinessException;
import com.swift.sportspub.common.exception.ErrorCode;
import com.swift.sportspub.pub.dto.BusinessHourItem;
import com.swift.sportspub.pub.dto.BusinessStatus;
import com.swift.sportspub.pub.dto.MenuItem;
import com.swift.sportspub.pub.dto.PubDetailResponse;
import com.swift.sportspub.pub.dto.PubFilterParams;
import com.swift.sportspub.pub.dto.PubImageItem;
import com.swift.sportspub.pub.dto.PubListItem;
import com.swift.sportspub.pub.dto.PubListResponse;
import com.swift.sportspub.pub.dto.PubListSearchCondition;
import com.swift.sportspub.pub.dto.PubMapMarker;
import com.swift.sportspub.pub.dto.PubMapResponse;
import com.swift.sportspub.pub.dto.PubMapSearchCondition;
import com.swift.sportspub.pub.dto.PubSearchFilter;
import com.swift.sportspub.pub.dto.PubSummary;
import com.swift.sportspub.pub.dto.SupportedTeamDetail;
import com.swift.sportspub.pub.dto.SupportedTeamSummary;
import com.swift.sportspub.pub.entity.Pub;
import com.swift.sportspub.pub.entity.PubBusinessHours;
import com.swift.sportspub.pub.entity.PubFacility;
import com.swift.sportspub.pub.entity.PubFoodTag;
import com.swift.sportspub.pub.entity.PubImage;
import com.swift.sportspub.pub.entity.PubStyle;
import com.swift.sportspub.pub.entity.PubTheme;
import com.swift.sportspub.pub.repository.MenuRepository;
import com.swift.sportspub.pub.repository.PubBusinessHoursRepository;
import com.swift.sportspub.pub.repository.PubFacilityRepository;
import com.swift.sportspub.pub.repository.PubFoodTagRepository;
import com.swift.sportspub.pub.repository.PubImageRepository;
import com.swift.sportspub.pub.repository.PubRepository;
import com.swift.sportspub.pub.repository.PubRepositoryCustom;
import com.swift.sportspub.pub.repository.PubStyleRepository;
import com.swift.sportspub.pub.repository.PubSupportedTeamRepository;
import com.swift.sportspub.pub.repository.PubSupportedTeamRepository.SupportedTeamRow;
import com.swift.sportspub.pub.repository.PubThemeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PubQueryService {

    private final PubRepository pubRepository;
    private final PubImageRepository pubImageRepository;
    private final PubSupportedTeamRepository pubSupportedTeamRepository;
    private final PubFacilityRepository pubFacilityRepository;
    private final PubStyleRepository pubStyleRepository;
    private final PubThemeRepository pubThemeRepository;
    private final PubFoodTagRepository pubFoodTagRepository;
    private final PubBusinessHoursRepository pubBusinessHoursRepository;
    private final MenuRepository menuRepository;

    @Transactional(readOnly = true)
    public PubMapResponse findMapMarkers(BigDecimal swLat, BigDecimal swLng,
                                         BigDecimal neLat, BigDecimal neLng,
                                         PubFilterParams filter) {
        if (swLat == null || swLng == null || neLat == null || neLng == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT);
        }
        if (swLat.compareTo(neLat) > 0 || swLng.compareTo(neLng) > 0) {
            throw new BusinessException(ErrorCode.INVALID_INPUT);
        }

        PubMapSearchCondition condition = new PubMapSearchCondition(
                swLat, swLng, neLat, neLng, buildSearchFilter(filter));

        List<Long> pubIds = pubRepository.searchMapPubIds(condition);
        if (pubIds.isEmpty()) {
            return PubMapResponse.of(List.of());
        }

        Map<Long, Pub> pubById = new HashMap<>();
        for (Pub pub : pubRepository.findAllById(pubIds)) {
            pubById.put(pub.getPubId(), pub);
        }
        PubChildAggregates children = loadChildAggregates(pubIds);

        List<PubMapMarker> markers = new ArrayList<>(pubIds.size());
        for (Long pubId : pubIds) {
            Pub pub = pubById.get(pubId);
            if (pub == null) {
                continue;
            }
            markers.add(new PubMapMarker(
                    pub.getPubId(),
                    pub.getName(),
                    pub.getLatitude(),
                    pub.getLongitude(),
                    pub.getStatus(),
                    pub.getFavoriteCount(),
                    children.thumbnails().get(pubId),
                    children.supportedTeams().getOrDefault(pubId, List.of()),
                    children.facilities().getOrDefault(pubId, List.of()),
                    children.styles().getOrDefault(pubId, List.of()),
                    children.themes().getOrDefault(pubId, List.of()),
                    children.foods().getOrDefault(pubId, List.of())
            ));
        }
        return PubMapResponse.of(markers);
    }

    @Transactional(readOnly = true)
    public PubListResponse findList(String keyword, PubFilterParams filter, int page, int size) {
        PubListSearchCondition condition = new PubListSearchCondition(
                keyword, buildSearchFilter(filter), page, size);

        PubRepositoryCustom.PubIdPage idPage = pubRepository.searchPubIds(condition);
        List<Long> pubIds = idPage.pubIds();

        if (pubIds.isEmpty()) {
            return PubListResponse.of(List.of(), condition.page(), condition.size(), idPage.totalElements());
        }

        Map<Long, Pub> pubById = new HashMap<>();
        for (Pub pub : pubRepository.findAllById(pubIds)) {
            pubById.put(pub.getPubId(), pub);
        }
        PubChildAggregates children = loadChildAggregates(pubIds);

        Map<Long, List<PubBusinessHours>> hoursByPubId = new HashMap<>();
        for (PubBusinessHours h : pubBusinessHoursRepository.findAllByPubIdIn(pubIds)) {
            hoursByPubId.computeIfAbsent(h.getPubId(), k -> new ArrayList<>()).add(h);
        }

        LocalDateTime now = LocalDateTime.now();
        List<PubListItem> content = new ArrayList<>(pubIds.size());
        for (Long pubId : pubIds) {
            Pub pub = pubById.get(pubId);
            if (pub == null) {
                continue;
            }
            BusinessStatus status = BusinessStatus.calculate(
                    hoursByPubId.getOrDefault(pubId, List.of()), now);
            content.add(new PubListItem(
                    pub.getPubId(),
                    pub.getName(),
                    pub.getRegion(),
                    pub.getAddress(),
                    children.thumbnails().get(pubId),
                    pub.getFavoriteCount(),
                    status,
                    children.supportedTeams().getOrDefault(pubId, List.of()),
                    children.facilities().getOrDefault(pubId, List.of()),
                    children.styles().getOrDefault(pubId, List.of()),
                    children.themes().getOrDefault(pubId, List.of()),
                    children.foods().getOrDefault(pubId, List.of())
            ));
        }
        return PubListResponse.of(content, condition.page(), condition.size(), idPage.totalElements());
    }

    @Transactional(readOnly = true)
    public PubDetailResponse findDetail(Long pubId) {
        Pub pub = pubRepository.findById(pubId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));

        List<Long> ids = List.of(pubId);

        List<PubImageItem> images = pubImageRepository
                .findAllByPubIdOrderByDisplayOrderAscImageIdAsc(pubId)
                .stream().map(PubImageItem::from).toList();

        List<SupportedTeamDetail> supportedTeams = pubSupportedTeamRepository
                .findAllRowsByPubIdIn(ids).stream()
                .map(r -> new SupportedTeamDetail(r.getTeamId(), r.getShortName(), r.getName()))
                .toList();

        List<String> facilityCodes = pubFacilityRepository.findAllByPubIdIn(ids).stream()
                .map(f -> f.getFacilityCode().name()).toList();
        List<String> styleCodes = pubStyleRepository.findAllByPubIdIn(ids).stream()
                .map(s -> s.getStyleCode().name()).toList();
        List<String> themeCodes = pubThemeRepository.findAllByPubIdIn(ids).stream()
                .map(t -> t.getThemeCode().name()).toList();
        List<String> foodCodes = pubFoodTagRepository.findAllByPubIdIn(ids).stream()
                .map(ft -> ft.getFoodCode().name()).toList();

        List<BusinessHourItem> businessHours = pubBusinessHoursRepository
                .findAllByPubIdOrderByDayOfWeekAsc(pubId)
                .stream().map(BusinessHourItem::from).toList();

        List<MenuItem> menus = menuRepository
                .findAllByPubIdOrderByDisplayOrderAscMenuIdAsc(pubId)
                .stream().map(MenuItem::from).toList();

        return new PubDetailResponse(
                pub.getPubId(),
                pub.getName(),
                pub.getAddress(),
                pub.getRegion(),
                pub.getLatitude(),
                pub.getLongitude(),
                pub.getPhone(),
                pub.getStatus(),
                pub.getCapacityRange(),
                pub.getGroupSeatMaxPeople(),
                pub.getFavoriteCount(),
                pub.getDescription(),
                images,
                supportedTeams,
                facilityCodes,
                styleCodes,
                themeCodes,
                foodCodes,
                businessHours,
                menus
        );
    }

    @Transactional(readOnly = true)
    public PubSummary findSummary(Long pubId) {
        Pub pub = pubRepository.findById(pubId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));

        String thumbnailUrl = pubImageRepository
                .findFirstByPubIdOrderByDisplayOrderAsc(pubId)
                .map(image -> image.getImageUrl())
                .orElse(null);

        return PubSummary.of(pub, thumbnailUrl);
    }

    @Transactional(readOnly = true)
    public List<PubSummary> findSummariesByIds(List<Long> pubIds) {
        if (pubIds == null || pubIds.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long, Pub> pubById = new HashMap<>();
        for (Pub pub : pubRepository.findAllById(pubIds)) {
            pubById.put(pub.getPubId(), pub);
        }

        Map<Long, String> thumbnailByPubId = new HashMap<>();
        for (PubImage image : pubImageRepository.findAllByPubIdInOrderByPubIdAscDisplayOrderAsc(pubIds)) {
            thumbnailByPubId.putIfAbsent(image.getPubId(), image.getImageUrl());
        }

        List<PubSummary> summaries = new ArrayList<>(pubIds.size());
        for (Long pubId : pubIds) {
            Pub pub = pubById.get(pubId);
            if (pub == null) {
                continue;
            }
            summaries.add(PubSummary.of(pub, thumbnailByPubId.get(pubId)));
        }
        return summaries;
    }

    private PubSearchFilter buildSearchFilter(PubFilterParams filter) {
        RegionFilter regionFilter = RegionResolver.resolve(filter.regions());
        return new PubSearchFilter(
                filter.teamIds(), regionFilter.regions(), regionFilter.subRegion(),
                filter.facilityCodes(), filter.styleCodes(), filter.themeCodes(), filter.foodCodes(),
                filter.capacityRange(), filter.openNow(), filter.businessDay()
        );
    }

    private PubChildAggregates loadChildAggregates(List<Long> pubIds) {
        Map<Long, String> thumbnailByPubId = new HashMap<>();
        for (PubImage image : pubImageRepository.findAllByPubIdInOrderByPubIdAscDisplayOrderAsc(pubIds)) {
            thumbnailByPubId.putIfAbsent(image.getPubId(), image.getImageUrl());
        }

        Map<Long, List<SupportedTeamSummary>> supportedTeamsByPubId = new HashMap<>();
        for (SupportedTeamRow row : pubSupportedTeamRepository.findAllRowsByPubIdIn(pubIds)) {
            supportedTeamsByPubId
                    .computeIfAbsent(row.getPubId(), k -> new ArrayList<>())
                    .add(new SupportedTeamSummary(row.getTeamId(), row.getShortName()));
        }

        Map<Long, List<String>> facilitiesByPubId = groupCodes(
                pubFacilityRepository.findAllByPubIdIn(pubIds),
                PubFacility::getPubId,
                pf -> pf.getFacilityCode().name());
        Map<Long, List<String>> stylesByPubId = groupCodes(
                pubStyleRepository.findAllByPubIdIn(pubIds),
                PubStyle::getPubId,
                ps -> ps.getStyleCode().name());
        Map<Long, List<String>> themesByPubId = groupCodes(
                pubThemeRepository.findAllByPubIdIn(pubIds),
                PubTheme::getPubId,
                pt -> pt.getThemeCode().name());
        Map<Long, List<String>> foodsByPubId = groupCodes(
                pubFoodTagRepository.findAllByPubIdIn(pubIds),
                PubFoodTag::getPubId,
                pft -> pft.getFoodCode().name());

        return new PubChildAggregates(
                thumbnailByPubId, supportedTeamsByPubId,
                facilitiesByPubId, stylesByPubId, themesByPubId, foodsByPubId
        );
    }

    private <T> Map<Long, List<String>> groupCodes(List<T> rows,
                                                   java.util.function.Function<T, Long> keyFn,
                                                   java.util.function.Function<T, String> valueFn) {
        Map<Long, List<String>> grouped = new HashMap<>();
        for (T row : rows) {
            grouped.computeIfAbsent(keyFn.apply(row), k -> new ArrayList<>()).add(valueFn.apply(row));
        }
        return grouped;
    }

    private record PubChildAggregates(
            Map<Long, String> thumbnails,
            Map<Long, List<SupportedTeamSummary>> supportedTeams,
            Map<Long, List<String>> facilities,
            Map<Long, List<String>> styles,
            Map<Long, List<String>> themes,
            Map<Long, List<String>> foods
    ) {
    }
}
