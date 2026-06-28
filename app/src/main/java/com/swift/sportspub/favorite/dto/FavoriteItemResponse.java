package com.swift.sportspub.favorite.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "즐겨찾기 항목")
public record FavoriteItemResponse(

        @Schema(description = "즐겨찾기 ID", example = "1")
        Long favoriteId,

        @Schema(description = "펍 ID", example = "12")
        Long pubId,

        @Schema(description = "펍 이름 (Pub 도메인 연동 후 제공)", example = "시그니처 펍")
        String pubName,

        @Schema(description = "대표 이미지 URL (Pub 도메인 연동 후 제공, 없으면 null)", example = "https://example.com/image.jpg")
        String thumbnailImageUrl
) {
}
