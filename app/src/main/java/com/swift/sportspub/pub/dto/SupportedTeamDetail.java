package com.swift.sportspub.pub.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "상영 구단 상세 (상세 응답용)")
public record SupportedTeamDetail(

        @Schema(description = "구단 ID", example = "1")
        Long teamId,

        @Schema(description = "구단 약칭", example = "LG")
        String shortName,

        @Schema(description = "구단 정식 명칭", example = "LG 트윈스")
        String name
) {
}
