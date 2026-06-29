package com.swift.sportspub.pub.dto;

import com.swift.sportspub.pub.entity.PubStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "지도 마커 1건 (BBox 조회 응답 요소)")
public record PubMapMarker(

        @Schema(description = "펍 ID", example = "1")
        Long pubId,

        @Schema(description = "펍 이름", example = "치어스 강남점")
        String name,

        @Schema(description = "위도", example = "37.4979")
        BigDecimal latitude,

        @Schema(description = "경도", example = "127.0276")
        BigDecimal longitude,

        @Schema(description = "영업 상태 (OPEN / CLOSED / TEMP_CLOSED)", example = "OPEN")
        PubStatus status,

        @Schema(description = "즐겨찾기 수", example = "24")
        Integer favoriteCount
) {
}
