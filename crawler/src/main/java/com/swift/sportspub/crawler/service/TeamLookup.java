package com.swift.sportspub.crawler.service;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class TeamLookup {

    private final JdbcTemplate jdbc;
    private final Map<String, Long> cache = new ConcurrentHashMap<>();

    public Long resolveOrThrow(String sportType, String shortName) {
        String key = sportType + ":" + shortName;
        Long cached = cache.get(key);
        if (cached != null) return cached;

        List<Long> ids = jdbc.queryForList(
                "SELECT team_id FROM teams WHERE sport_type = ? AND short_name = ?",
                Long.class, sportType, shortName
        );
        if (ids.isEmpty()) {
            throw new IllegalStateException(
                    "Unknown team: sport=" + sportType + ", short=" + shortName
            );
        }
        Long id = ids.get(0);
        cache.put(key, id);
        return id;
    }
}
