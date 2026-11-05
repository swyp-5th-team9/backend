package com.swift.sportspub.notification.scheduler;

import com.swift.sportspub.notification.service.GameReminderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;

@Slf4j
@Component
@RequiredArgsConstructor
public class GameReminderScheduler {

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    private final GameReminderService gameReminderService;

    @Scheduled(cron = "${app.notification.reminder-cron:0 0 9 * * *}", zone = "Asia/Seoul")
    public void createDailyGameReminders() {
        LocalDate today = LocalDate.now(KST);
        int created = gameReminderService.createDailyReminders(today);
        log.info("Created {} game reminders for {}", created, today);
    }
}
