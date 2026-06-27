package com.swift.sportspub.crawler.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class GameData {

    private LocalDate gameDate;
    private String startTime;
    private String homeTeam;
    private String awayTeam;
    private String stadium;
    private Integer homeScore;
    private Integer awayScore;
    private Status status;

    public enum Status {
        SCHEDULED,
        LIVE,
        FINISHED,
        CANCELED,
        POSTPONED
    }
}
