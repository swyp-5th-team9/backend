package com.swift.sportspub.favorite.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

@Schema(description = "즐겨찾기 삭제 요청")
public record FavoriteDeleteRequest(

        @Schema(description = "삭제할 즐겨찾기 ID 목록 (1개 이상)", example = "[1, 3, 5]")
        @NotEmpty(message = "favoriteIds는 1개 이상이어야 합니다.")
        List<Long> favoriteIds
) {
}
