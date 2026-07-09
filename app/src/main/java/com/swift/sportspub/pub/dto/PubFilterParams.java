package com.swift.sportspub.pub.dto;

import com.swift.sportspub.pub.entity.CapacityRange;
import io.swagger.v3.oas.annotations.Parameter;

import java.util.List;

public record PubFilterParams(
        @Parameter(description = "상영 구단 ID (단일, 호환용)", example = "1")
        Long teamId,

        @Parameter(description = "상영 구단 ID 다중 (OR 매칭, 입력 중 하나라도 응원)")
        List<Long> teamIds,

        @Parameter(description = "지역 — 자치구 코드/광역 코드/sub 코드(JAMSIL, HONGDAE_HAPJEONG, SANGAM_MANGWON)", example = "GANGNAM")
        String region,

        @Parameter(description = "시설 코드 (AND, 예: GROUP_SEAT, PARKING)")
        List<String> facilityCodes,

        @Parameter(description = "스타일 코드 (AND, 예: BIG_SCREEN)")
        List<String> styleCodes,

        @Parameter(description = "테마 코드 (AND, 예: SPACIOUS_VIEW)")
        List<String> themeCodes,

        @Parameter(description = "음식 코드 (AND, 예: CHICKEN, BEER)")
        List<String> foodCodes,

        @Parameter(description = "수용 규모", example = "R_50_100")
        CapacityRange capacityRange,

        @Parameter(description = "지금 영업중인 펍만 (true)")
        Boolean openNow,

        @Parameter(description = "영업요일 — EVERYDAY/WEEKDAY/WEEKEND/MON..SUN (선택 요일 전부 영업)")
        BusinessDayFilter businessDay
) {
    public List<Long> mergedTeamIds() {
        if (teamIds != null && !teamIds.isEmpty()) {
            return teamIds;
        }
        if (teamId != null) {
            return List.of(teamId);
        }
        return List.of();
    }
}
