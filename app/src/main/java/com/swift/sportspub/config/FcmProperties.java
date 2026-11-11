package com.swift.sportspub.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * FCM(Firebase Cloud Messaging) 설정 ({@code app.fcm.*}).
 *
 * <p>운영에서는 {@code FCM_ENABLED=true} 와 서비스 계정 JSON
 * ({@code FCM_CREDENTIALS_JSON} 또는 Base64 {@code FCM_CREDENTIALS_BASE64}) 를 주입한다.
 * 미설정 시 푸시 전송은 no-op 이다.
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.fcm")
public class FcmProperties {

    /**
     * 푸시 발송 활성화 여부. credentials 가 없어도 false 이면 발송하지 않는다.
     */
    private boolean enabled = false;

    /**
     * Firebase 서비스 계정 JSON 원문.
     */
    private String credentialsJson;

    /**
     * Firebase 서비스 계정 JSON 의 Base64 (systemd env 이스케이프 회피용).
     */
    private String credentialsBase64;

    public boolean isConfigured() {
        return enabled && (StringUtils.hasText(credentialsJson) || StringUtils.hasText(credentialsBase64));
    }
}
