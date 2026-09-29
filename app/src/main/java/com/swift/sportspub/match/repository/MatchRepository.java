package com.swift.sportspub.match.repository;

import com.swift.sportspub.match.entity.Match;
import com.swift.sportspub.team.entity.SportType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface MatchRepository extends JpaRepository<Match, Long> {

    @Query("""
            SELECT m FROM Match m
            JOIN FETCH m.homeTeam
            JOIN FETCH m.awayTeam
            WHERE m.sportType = :sportType
              AND m.matchDate BETWEEN :from AND :to
              AND (:teamId IS NULL
                   OR m.homeTeam.teamId = :teamId
                   OR m.awayTeam.teamId = :teamId)
            ORDER BY m.matchDate ASC, m.startTime ASC, m.matchId ASC
            """)
    List<Match> findSchedule(
            @Param("sportType") SportType sportType,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to,
            @Param("teamId") Long teamId
    );
}
