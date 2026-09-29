package com.swift.sportspub.favorite.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "즐겨찾기 목록 조회 응답")
public record FavoriteListResponse(

        @Schema(description = "즐겨찾기 목록 (createdAt 내림차순, 최대 30개)")
        List<FavoriteItemResponse> favorites
) {
    public static FavoriteListResponse of(List<FavoriteItemResponse> favorites) {
        return new FavoriteListResponse(favorites);
    }
}
