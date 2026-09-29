package com.swift.sportspub.crawler.provider;

import com.swift.sportspub.crawler.dto.GameData;

import java.util.List;

public interface GameDataProvider {
    List<GameData> fetchGames(int year, int month);
}
