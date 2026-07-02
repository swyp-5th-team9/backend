package com.swift.sportspub.pub.dto;

import com.swift.sportspub.pub.entity.CapacityRange;
import com.swift.sportspub.pub.entity.Region;
import com.swift.sportspub.pub.entity.SubRegion;

import java.util.List;

public record PubListSearchCondition(
        String keyword,
        List<Long> teamIds,
        List<Region> regions,
        SubRegion subRegion,
        List<String> facilityCodes,
        List<String> styleCodes,
        List<String> themeCodes,
        List<String> foodCodes,
        CapacityRange capacityRange,
        Boolean openNow,
        BusinessDayFilter businessDay,
        int page,
        int size
) {
    public PubListSearchCondition {
        teamIds = teamIds == null ? List.of() : teamIds;
        facilityCodes = facilityCodes == null ? List.of() : facilityCodes;
        styleCodes = styleCodes == null ? List.of() : styleCodes;
        themeCodes = themeCodes == null ? List.of() : themeCodes;
        foodCodes = foodCodes == null ? List.of() : foodCodes;
        regions = regions == null ? List.of() : regions;
    }
}
