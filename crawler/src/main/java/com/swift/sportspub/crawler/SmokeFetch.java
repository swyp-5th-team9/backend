package com.swift.sportspub.crawler;

import com.swift.sportspub.crawler.dto.GameData;
import com.swift.sportspub.crawler.provider.KboPlaywrightProvider;

import java.time.LocalDate;
import java.util.List;

public class SmokeFetch {
    public static void main(String[] args) {
        int year = args.length >= 1 ? Integer.parseInt(args[0]) : LocalDate.now().getYear();
        int month = args.length >= 2 ? Integer.parseInt(args[1]) : LocalDate.now().getMonthValue();

        System.out.printf("==== KBO fetch %d-%02d ====%n", year, month);
        KboPlaywrightProvider provider = new KboPlaywrightProvider();
        List<GameData> games = provider.fetchGames(year, month);

        System.out.printf("==== 총 %d건 ====%n", games.size());
        for (GameData g : games) {
            System.out.printf("%s %5s | %-6s @ %-6s | %-12s | %s-%s [%s]%n",
                    g.getGameDate(),
                    g.getStartTime() == null ? "" : g.getStartTime(),
                    g.getAwayTeam(),
                    g.getHomeTeam(),
                    g.getStadium() == null ? "" : g.getStadium(),
                    g.getHomeScore() == null ? "-" : g.getHomeScore().toString(),
                    g.getAwayScore() == null ? "-" : g.getAwayScore().toString(),
                    g.getStatus());
        }
    }
}
