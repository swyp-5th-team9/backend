package com.swift.sportspub.crawler.provider;

import com.swift.sportspub.crawler.dto.GameData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@ConditionalOnProperty(name = "crawler.provider", havingValue = "thesportsdb")
public class TheSportsDbProvider implements GameDataProvider {

    @Override
    public List<GameData> fetchGames(int year, int month) {
        log.warn("[TheSportsDB] 아직 구현되지 않은 provider입니다.");
        return List.of();
    }
}
