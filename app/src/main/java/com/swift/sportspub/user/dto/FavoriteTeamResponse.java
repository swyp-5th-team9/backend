package com.swift.sportspub.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        description = "선호 구단 응답",
        example = "{\"teamId\":1,\"teamName\":\"LG\"}"
)
public record FavoriteTeamResponse(

        @Schema(description = "구단 ID", example = "1")
        Long teamId,

        @Schema(description = "구단명", example = "LG")
        String teamName
) {
}
