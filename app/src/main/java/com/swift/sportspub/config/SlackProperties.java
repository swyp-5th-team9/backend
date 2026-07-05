package com.swift.sportspub.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Slack Incoming Webhook 설정 ({@code app.slack.*}).
 *
 * <p>설정 키: {@code app.slack.report-webhook-url}
 * (로컬은 {@code application-local.yml} / {@code SLACK_REPORT_WEBHOOK_URL} 환경 변수 매핑)
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.slack")
public class SlackProperties {

    private String reportWebhookUrl;

    public boolean isReportWebhookConfigured() {
        return StringUtils.hasText(reportWebhookUrl);
    }
}
