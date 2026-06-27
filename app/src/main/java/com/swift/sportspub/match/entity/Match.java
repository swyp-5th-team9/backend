package com.swift.sportspub.match.entity;

import com.swift.sportspub.common.entity.BaseEntity;
import com.swift.sportspub.team.entity.SportType;
import com.swift.sportspub.team.entity.Team;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
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
public class Match extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "match_id")
    private Long matchId;

    @Enumerated(EnumType.STRING)
    @Column(name = "sport_type", nullable = false, length = 20)
    private SportType sportType;

    @Column(name = "match_date", nullable = false)
    private LocalDate matchDate;

    @Column(name = "start_time")
    private LocalTime startTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "home_team_id", nullable = false)
    private Team homeTeam;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "away_team_id", nullable = false)
    private Team awayTeam;

    @Column(name = "stadium", length = 50)
    private String stadium;

    @Column(name = "home_score")
    private Integer homeScore;

    @Column(name = "away_score")
    private Integer awayScore;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private MatchStatus status;
}
