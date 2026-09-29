package com.swift.sportspub.match.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.swift.sportspub.match.entity.Match;
import com.swift.sportspub.match.entity.MatchStatus;
import com.swift.sportspub.team.entity.SportType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalTime;

@Schema(description = "경기 1건 요약 (목록 응답용)")
public record MatchSummary(

        @Schema(description = "경기 ID", example = "101")
        Long matchId,

        @Schema(description = "스포츠 종목", example = "KBO")
        SportType sportType,

        @Schema(description = "경기 날짜 (YYYY-MM-DD)", example = "2026-06-26")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        LocalDate matchDate,

        @Schema(description = "경기 시작 시각 (HH:mm)", example = "18:30")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
        LocalTime startTime,

        @Schema(description = "구장 정식 명칭", example = "서울 잠실야구장")
        String stadium,

        @Schema(description = "경기 상태", example = "SCHEDULED")
        MatchStatus status,

        @Schema(description = "홈 팀")
        TeamSummary homeTeam,

        @Schema(description = "원정 팀")
        TeamSummary awayTeam,

        @Schema(description = "홈 팀 점수 (종료 전 null)", example = "5")
        Integer homeScore,

        @Schema(description = "원정 팀 점수 (종료 전 null)", example = "3")
        Integer awayScore
) {
    public static MatchSummary from(Match match) {
        return new MatchSummary(
                match.getMatchId(),
                match.getSportType(),
                match.getMatchDate(),
                match.getStartTime(),
                match.getStadium(),
                match.getStatus(),
                TeamSummary.from(match.getHomeTeam()),
                TeamSummary.from(match.getAwayTeam()),
                match.getHomeScore(),
                match.getAwayScore()
        );
    }
}
