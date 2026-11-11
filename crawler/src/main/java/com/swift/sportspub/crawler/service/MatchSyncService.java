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

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
        Set<String> seenKeys = new HashSet<>();
        int created = 0;
        int updated = 0;

        for (GameData game : games) {
            Long homeTeamId = teamLookup.resolveOrThrow(DEFAULT_SPORT_TYPE, game.getHomeTeam());
            Long awayTeamId = teamLookup.resolveOrThrow(DEFAULT_SPORT_TYPE, game.getAwayTeam());
            String key = naturalKey(game.getGameDate(), homeTeamId, awayTeamId);
            seenKeys.add(key);

            if (upsert(game, homeTeamId, awayTeamId)) {
                created++;
            } else {
                updated++;
            }
        }

        int deleted = reconcileOrphans(year, month, seenKeys);
        log.info(
                "동기화 완료: {}년 {}월 - 신규 {}건, 갱신 {}건, 삭제(공식 없음) {}건",
                year, month, created, updated, deleted
        );
    }

    private boolean upsert(GameData game, Long homeTeamId, Long awayTeamId) {
        MatchStatus status = MatchStatus.valueOf(game.getStatus().name());
        LocalTime startTime = parseTime(game.getStartTime());
        String stadium = stadiumNameResolver.resolve(game.getStadium());

        Optional<Match> existing = matchRepository
                .findBySportTypeAndMatchDateAndHomeTeamIdAndAwayTeamId(
                        DEFAULT_SPORT_TYPE, game.getGameDate(), homeTeamId, awayTeamId
                );

        if (existing.isPresent()) {
            existing.get().applySnapshot(
                    startTime,
                    stadium,
                    game.getHomeScore(),
                    game.getAwayScore(),
                    status
            );
            return false;
        }

        matchRepository.save(Match.builder()
                .sportType(DEFAULT_SPORT_TYPE)
                .matchDate(game.getGameDate())
                .startTime(startTime)
                .homeTeamId(homeTeamId)
                .awayTeamId(awayTeamId)
                .stadium(stadium)
                .homeScore(game.getHomeScore())
                .awayScore(game.getAwayScore())
                .status(status)
                .build());
        return true;
    }

    /**
     * 해당 연·월 DB 경기 중 이번 크롤 결과에 없는 row 를 삭제한다.
     * (잘못된 month 스탬프로 생긴 유령 일정 제거)
     */
    private int reconcileOrphans(int year, int month, Set<String> seenKeys) {
        YearMonth ym = YearMonth.of(year, month);
        LocalDate from = ym.atDay(1);
        LocalDate to = ym.atEndOfMonth();
        List<Match> existing = matchRepository.findBySportTypeAndMatchDateBetween(
                DEFAULT_SPORT_TYPE, from, to
        );

        int deleted = 0;
        for (Match match : existing) {
            String key = naturalKey(match.getMatchDate(), match.getHomeTeamId(), match.getAwayTeamId());
            if (!seenKeys.contains(key)) {
                matchRepository.delete(match);
                deleted++;
            }
        }
        return deleted;
    }

    private String naturalKey(LocalDate matchDate, Long homeTeamId, Long awayTeamId) {
        return matchDate + "|" + homeTeamId + "|" + awayTeamId;
    }

    private LocalTime parseTime(String hhmm) {
        if (hhmm == null || hhmm.isBlank()) {
            return null;
        }
        try {
            return LocalTime.parse(hhmm);
        } catch (DateTimeParseException e) {
            log.warn("시작 시각 파싱 실패: {}", hhmm);
            return null;
        }
    }
}
