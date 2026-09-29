package com.swift.sportspub.pub.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "지도 펍 조회 응답")
public record PubMapResponse(

        @Schema(description = "BBox 안 마커 목록")
        List<PubMapMarker> pubs
) {
    public static PubMapResponse of(List<PubMapMarker> pubs) {
        return new PubMapResponse(pubs);
    }
}
