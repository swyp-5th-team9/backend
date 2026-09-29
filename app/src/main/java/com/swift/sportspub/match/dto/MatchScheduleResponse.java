package com.swift.sportspub.match.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "경기 일정 조회 응답")
public record MatchScheduleResponse(

        @Schema(description = "경기 목록 (matchDate, startTime 오름차순)")
        List<MatchSummary> matches
) {
    public static MatchScheduleResponse of(List<MatchSummary> matches) {
        return new MatchScheduleResponse(matches);
    }
}
