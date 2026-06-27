package com.swift.sportspub.crawler.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(
        name = "matches",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_matches_natural",
                columnNames = {"sport_type", "match_date", "home_team_id", "away_team_id"}
        )
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "matchId", callSuper = false)
public class Match {

    private static final String DEFAULT_SPORT_TYPE = "KBO";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "match_id")
    private Long matchId;

    @Column(name = "sport_type", nullable = false, length = 20)
    private String sportType;

    @Column(name = "match_date", nullable = false)
    private LocalDate matchDate;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "home_team_id", nullable = false)
    private Long homeTeamId;

    @Column(name = "away_team_id", nullable = false)
    private Long awayTeamId;

    @Column(name = "stadium", length = 50)
    private String stadium;

    @Column(name = "home_score")
    private Integer homeScore;

    @Column(name = "away_score")
    private Integer awayScore;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private MatchStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    private Match(String sportType, LocalDate matchDate, LocalTime startTime,
                  Long homeTeamId, Long awayTeamId, String stadium,
                  Integer homeScore, Integer awayScore, MatchStatus status) {
        this.sportType = sportType != null ? sportType : DEFAULT_SPORT_TYPE;
        this.matchDate = matchDate;
        this.startTime = startTime;
        this.homeTeamId = homeTeamId;
        this.awayTeamId = awayTeamId;
        this.stadium = stadium;
        this.homeScore = homeScore;
        this.awayScore = awayScore;
        this.status = status;
    }

    public void updateResult(Integer homeScore, Integer awayScore, MatchStatus status) {
        this.homeScore = homeScore;
        this.awayScore = awayScore;
        this.status = status;
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
