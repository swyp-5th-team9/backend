package com.swift.sportspub.crawler.repository;

import com.swift.sportspub.crawler.entity.Match;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface MatchRepository extends JpaRepository<Match, Long> {

    Optional<Match> findBySportTypeAndMatchDateAndHomeTeamIdAndAwayTeamId(
            String sportType,
            LocalDate matchDate,
            Long homeTeamId,
            Long awayTeamId
    );
}
