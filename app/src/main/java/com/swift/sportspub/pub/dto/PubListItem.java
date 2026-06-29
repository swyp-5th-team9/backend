package com.swift.sportspub.pub.dto;

import com.swift.sportspub.pub.entity.Region;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "펍 목록 1건")
public record PubListItem(

        @Schema(description = "펍 ID", example = "1")
        Long pubId,

        @Schema(description = "펍 이름", example = "치어스 강남점")
        String name,

        @Schema(description = "지역 코드 (자치구)", example = "GANGNAM")
        Region region,

        @Schema(description = "주소", example = "서울 강남구 ...")
        String address,

        @Schema(description = "대표 이미지 URL (없을 시 null)", example = "https://cdn.example.com/pubs/1/1.jpg")
        String thumbnailUrl,

        @Schema(description = "즐겨찾기 수", example = "24")
        Integer favoriteCount,

        @Schema(description = "실시간 영업 상태", example = "OPEN_NOW")
        BusinessStatus businessStatus,

        @Schema(description = "상영 구단 (teamId/shortName)")
        List<SupportedTeamSummary> supportedTeams,

        @Schema(description = "시설 코드 목록 (V3 11종)")
        List<String> facilityCodes,

        @Schema(description = "펍스타일 코드 목록 (V3 6종)")
        List<String> styleCodes,

        @Schema(description = "테마 코드 목록 (V3 5종)")
        List<String> themeCodes,

        @Schema(description = "음식·주류 코드 목록 (V3 11종)")
        List<String> foodCodes
) {
}
