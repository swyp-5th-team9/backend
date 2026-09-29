package com.swift.sportspub.user.scheduler;

import com.swift.sportspub.user.config.UserWithdrawalProperties;
import com.swift.sportspub.user.service.UserHardDeleteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 미로그인 탈퇴 회원 Hard Delete 배치.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserHardDeleteScheduler {

    private final UserHardDeleteService userHardDeleteService;
    private final UserWithdrawalProperties userWithdrawalProperties;

    @Scheduled(cron = "${app.user.withdrawal.hard-delete-cron}")
    public void hardDeleteExpiredWithdrawnUsers() {
        int retriedCount = userHardDeleteService.retryFailedProfileImageDeletions();
        if (retriedCount > 0) {
            log.info("Retried and deleted {} failed profile images from S3", retriedCount);
        }

        LocalDateTime threshold = LocalDateTime.now()
                .minusDays(userWithdrawalProperties.getRetentionDays());
        int deletedCount = userHardDeleteService.hardDeleteExpiredWithdrawn(threshold);
        if (deletedCount > 0) {
            log.info("Hard-deleted {} withdrawn users deleted before {}", deletedCount, threshold);
        }
    }
}
