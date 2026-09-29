package com.swift.sportspub.user.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 회원 탈퇴·Hard Delete 정책 설정 ({@code app.user.withdrawal.*}).
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.user.withdrawal")
public class UserWithdrawalProperties {

    /**
     * Soft Delete 후 계정 복구 가능 일수. 경과 시 Hard Delete 대상.
     */
    private int retentionDays = 30;

    /**
     * 미로그인 탈퇴 회원 Hard Delete 배치 cron.
     */
    private String hardDeleteCron = "0 0 3 * * *";
}
