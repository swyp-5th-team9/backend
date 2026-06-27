package com.swift.sportspub.match.dto;

import com.swift.sportspub.team.entity.Team;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "경기 일정 응답에 포함되는 구단 요약")
public record TeamSummary(

        @Schema(description = "구단 ID", example = "1")
        Long teamId,

        @Schema(description = "구단 약칭", example = "LG")
        String shortName,

        @Schema(description = "구단 정식 명칭", example = "LG 트윈스")
        String name
) {
    public static TeamSummary from(Team team) {
        return new TeamSummary(team.getTeamId(), team.getShortName(), team.getName());
    }
}
