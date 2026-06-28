package com.swift.sportspub.pub.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "상영 구단 요약 (목록용)")
public record SupportedTeamSummary(

        @Schema(description = "구단 ID", example = "1")
        Long teamId,

        @Schema(description = "구단 약칭", example = "LG")
        String shortName
) {
}
