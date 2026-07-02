package com.swift.sportspub.favorite.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "즐겨찾기 항목")
public record FavoriteItemResponse(

        @Schema(description = "즐겨찾기 ID", example = "1")
        Long favoriteId,

        @Schema(description = "펍 ID", example = "12")
        Long pubId,

        @Schema(description = "펍 이름", example = "시그니처 펍")
        String pubName,

        @Schema(description = "지역 표시명 (subRegion 우선, 없으면 자치구명)", example = "홍대/합정")
        String region,

        @Schema(description = "대표 이미지 URL (display_order 최소, 없으면 null)", example = "https://example.com/pub1.jpg")
        String thumbnailImageUrl
) {
}
