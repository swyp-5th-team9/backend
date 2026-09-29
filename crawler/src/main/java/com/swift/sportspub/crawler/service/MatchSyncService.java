package com.swift.sportspub.crawler.service;

import com.swift.sportspub.crawler.dto.GameData;
import com.swift.sportspub.crawler.entity.Match;
import com.swift.sportspub.crawler.entity.MatchStatus;
import com.swift.sportspub.crawler.provider.GameDataProvider;
import com.swift.sportspub.crawler.repository.MatchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MatchSyncService {

    private static final String DEFAULT_SPORT_TYPE = "KBO";

    private final GameDataProvider provider;
    private final MatchRepository matchRepository;
    private final TeamLookup teamLookup;
    private final StadiumNameResolver stadiumNameResolver;

    @Transactional
    public void syncCurrentAndNextMonth() {
        YearMonth current = YearMonth.now();
        syncMonth(current.getYear(), current.getMonthValue());
        YearMonth next = current.plusMonths(1);
        syncMonth(next.getYear(), next.getMonthValue());
    }

    @Transactional
    public void syncMonth(int year, int month) {
        List<GameData> games = provider.fetchGames(year, month);
        int created = 0;
        int updated = 0;
        for (GameData game : games) {
            if (upsert(game)) {
                created++;
            } else {
                updated++;
            }
        }
        log.info("동기화 완료: {}년 {}월 - 신규 {}건, 갱신 {}건", year, month, created, updated);
    }

    private boolean upsert(GameData game) {
        Long homeTeamId = teamLookup.resolveOrThrow(DEFAULT_SPORT_TYPE, game.getHomeTeam());
        Long awayTeamId = teamLookup.resolveOrThrow(DEFAULT_SPORT_TYPE, game.getAwayTeam());
        MatchStatus status = MatchStatus.valueOf(game.getStatus().name());

        Optional<Match> existing = matchRepository
                .findBySportTypeAndMatchDateAndHomeTeamIdAndAwayTeamId(
                        DEFAULT_SPORT_TYPE, game.getGameDate(), homeTeamId, awayTeamId
                );

        if (existing.isPresent()) {
            if (status != MatchStatus.SCHEDULED) {
                existing.get().updateResult(game.getHomeScore(), game.getAwayScore(), status);
            }
            return false;
        }

        matchRepository.save(Match.builder()
                .sportType(DEFAULT_SPORT_TYPE)
                .matchDate(game.getGameDate())
                .startTime(parseTime(game.getStartTime()))
                .homeTeamId(homeTeamId)
                .awayTeamId(awayTeamId)
                .stadium(stadiumNameResolver.resolve(game.getStadium()))
                .homeScore(game.getHomeScore())
                .awayScore(game.getAwayScore())
                .status(status)
                .build());
        return true;
    }

    private LocalTime parseTime(String hhmm) {
        if (hhmm == null || hhmm.isBlank()) return null;
        try {
            return LocalTime.parse(hhmm);
        } catch (DateTimeParseException e) {
            log.warn("시작 시각 파싱 실패: {}", hhmm);
            return null;
        }
    }
}
