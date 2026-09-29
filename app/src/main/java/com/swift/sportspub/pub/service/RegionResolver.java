package com.swift.sportspub.pub.service;

import com.swift.sportspub.common.exception.BusinessException;
import com.swift.sportspub.common.exception.ErrorCode;
import com.swift.sportspub.pub.entity.Region;
import com.swift.sportspub.pub.entity.SubRegion;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class RegionResolver {

    // 구 단위 empty state 커버 관계: 요청 구 → 실제 조회 대상 구 목록 (민수님 확정안 · 피그마 코멘트)
    // NOWON(노원): 노원 + 강북
    // DOBONG(도봉): 도봉 + 성북
    // EUNPYEONG(은평): 은평 + 서대문
    // GANGSEO(강서): 강서 + 양천
    private static final Map<Region, List<Region>> COVERAGE = Map.of(
            Region.NOWON, List.of(Region.NOWON, Region.GANGBUK),
            Region.DOBONG, List.of(Region.DOBONG, Region.SEONGBUK),
            Region.EUNPYEONG, List.of(Region.EUNPYEONG, Region.SEODAEMUN),
            Region.GANGSEO, List.of(Region.GANGSEO, Region.YANGCHEON)
    );

    private RegionResolver() {
    }

    public static RegionFilter resolve(List<String> codes) {
        if (codes == null || codes.isEmpty()) {
            return RegionFilter.empty();
        }
        Set<Region> mergedRegions = new LinkedHashSet<>();
        Set<SubRegion> mergedSubs = new LinkedHashSet<>();
        for (String code : codes) {
            RegionFilter partial = resolve(code);
            mergedRegions.addAll(partial.regions());
            if (partial.subRegion() != null) {
                mergedSubs.add(partial.subRegion());
            }
        }
        SubRegion sub = mergedSubs.size() == 1 ? mergedSubs.iterator().next() : null;
        return new RegionFilter(List.copyOf(mergedRegions), sub);
    }

    public static RegionFilter resolve(String code) {
        if (code == null || code.isBlank()) {
            return RegionFilter.empty();
        }
        try {
            SubRegion sub = SubRegion.valueOf(code);
            return new RegionFilter(List.of(sub.getRegion()), sub);
        } catch (IllegalArgumentException ignored) {
        }
        try {
            Region region = Region.valueOf(code);
            List<Region> expanded = COVERAGE.getOrDefault(region, List.of(region));
            return new RegionFilter(expanded, null);
        } catch (IllegalArgumentException ignored) {
        }
        try {
            Region.Metro metro = Region.Metro.valueOf(code);
            List<Region> regions = Arrays.stream(Region.values())
                    .filter(r -> r.getMetro() == metro)
                    .toList();
            return new RegionFilter(regions, null);
        } catch (IllegalArgumentException ignored) {
        }
        throw new BusinessException(ErrorCode.INVALID_INPUT);
    }
}
