package com.swift.sportspub.pub.dto;

import com.swift.sportspub.pub.entity.CapacityRange;
import com.swift.sportspub.pub.entity.Region;

import java.util.List;

public record PubListSearchCondition(
        String keyword,
        Long teamId,
        List<Region> regions,
        List<String> facilityCodes,
        List<String> styleCodes,
        List<String> themeCodes,
        List<String> foodCodes,
        CapacityRange capacityRange,
        int page,
        int size
) {
    public PubListSearchCondition {
        facilityCodes = facilityCodes == null ? List.of() : facilityCodes;
        styleCodes = styleCodes == null ? List.of() : styleCodes;
        themeCodes = themeCodes == null ? List.of() : themeCodes;
        foodCodes = foodCodes == null ? List.of() : foodCodes;
        regions = regions == null ? List.of() : regions;
    }
}
