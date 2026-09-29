package com.swift.sportspub.pub.dto;

import com.swift.sportspub.pub.entity.PubStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.List;

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
        Integer favoriteCount,

        @Schema(description = "대표 이미지 URL (없을 시 null)", example = "https://cdn.example.com/pubs/1/1.jpg")
        String thumbnailUrl,

        @Schema(description = "상영 구단 (teamId/shortName)")
        List<SupportedTeamSummary> supportedTeams,

        @Schema(description = "시설 코드 목록")
        List<String> facilityCodes,

        @Schema(description = "펍스타일 코드 목록")
        List<String> styleCodes,

        @Schema(description = "테마 코드 목록")
        List<String> themeCodes,

        @Schema(description = "음식·주류 코드 목록")
        List<String> foodCodes
) {
}
