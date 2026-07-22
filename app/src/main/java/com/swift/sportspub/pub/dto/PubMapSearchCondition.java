package com.swift.sportspub.pub.dto;

import java.math.BigDecimal;

public record PubMapSearchCondition(
        BigDecimal swLat,
        BigDecimal swLng,
        BigDecimal neLat,
        BigDecimal neLng,
        PubSearchFilter filter
) {
}
