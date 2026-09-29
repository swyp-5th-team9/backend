package com.swift.sportspub.team.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "구단 목록 응답")
public record TeamListResponse(

        @Schema(description = "구단 목록 (team_id 오름차순)")
        List<TeamResponse> teams
) {
    public static TeamListResponse of(List<TeamResponse> teams) {
        return new TeamListResponse(teams);
    }
}
