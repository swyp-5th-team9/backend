package com.swift.sportspub.crawler.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("local")
@RequiredArgsConstructor
public class CrawlerRunner implements ApplicationRunner {

    private final MatchSyncService matchSyncService;

    @Override
    public void run(ApplicationArguments args) {
        log.info("[local] 앱 시작 시 즉시 동기화 실행");
        matchSyncService.syncCurrentAndNextMonth();
    }
}
