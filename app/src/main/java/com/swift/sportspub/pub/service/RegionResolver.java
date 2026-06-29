package com.swift.sportspub.pub.service;

import com.swift.sportspub.common.exception.BusinessException;
import com.swift.sportspub.common.exception.ErrorCode;
import com.swift.sportspub.pub.entity.Region;

import java.util.Arrays;
import java.util.List;

public final class RegionResolver {

    private RegionResolver() {
    }

    public static List<Region> resolve(String code) {
        if (code == null || code.isBlank()) {
            return List.of();
        }
        try {
            return List.of(Region.valueOf(code));
        } catch (IllegalArgumentException ignored) {
        }
        try {
            Region.Metro metro = Region.Metro.valueOf(code);
            return Arrays.stream(Region.values())
                    .filter(r -> r.getMetro() == metro)
                    .toList();
        } catch (IllegalArgumentException ignored) {
        }
        throw new BusinessException(ErrorCode.INVALID_INPUT);
    }
}
