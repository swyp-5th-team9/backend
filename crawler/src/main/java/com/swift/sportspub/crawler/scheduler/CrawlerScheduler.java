package com.swift.sportspub.crawler.scheduler;

import com.swift.sportspub.crawler.service.MatchSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CrawlerScheduler {

    private final MatchSyncService matchSyncService;

    @Scheduled(cron = "${crawler.schedule.cron}")
    public void scheduledSync() {
        log.info("스케줄 동기화 시작");
        matchSyncService.syncCurrentAndNextMonth();
    }
}
