package com.swift.sportspub.report.notification;

import com.swift.sportspub.config.SlackProperties;
import com.swift.sportspub.pub.entity.Pub;
import com.swift.sportspub.report.entity.Report;
import com.swift.sportspub.report.entity.ReportCategory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

/**
 * 제보 등록 Slack 알림.
 *
 * <p>Webhook URL이 없거나 전송에 실패해도 제보 등록 API에는 영향을 주지 않는다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SlackNotificationService {

    private static final int CONTENT_MAX_LENGTH = 300;
    private static final DateTimeFormatter CREATED_AT_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final SlackProperties slackProperties;
    private final SlackWebhookClient slackWebhookClient;

    public void notifyReportCreated(Report report, Pub pub, int imageCount) {
        try {
            if (!slackProperties.isReportWebhookConfigured()) {
                return;
            }
            slackWebhookClient.sendText(buildMessage(report, pub, imageCount));
        } catch (Exception e) {
            log.warn("Slack 제보 알림 전송 실패: reportId={}", report.getReportId(), e);
        }
    }

    private String buildMessage(Report report, Pub pub, int imageCount) {
        StringBuilder message = new StringBuilder();
        message.append("[새 제보 #%d] %s".formatted(
                report.getReportId(),
                formatCategory(report.getCategory())
        ));

        if (report.getCreatedAt() != null) {
            message.append(" | ").append(report.getCreatedAt().format(CREATED_AT_FORMAT));
        }

        message.append('\n');

        if (pub != null) {
            message.append("펍: ").append(pub.getName());
            if (pub.getPubId() != null) {
                message.append(" (pubId=").append(pub.getPubId()).append(')');
            }
            message.append('\n');
        }

        message.append("내용: ").append(truncateContent(report.getContent())).append('\n');
        message.append("이미지: ").append(imageCount).append('장');

        return message.toString();
    }

    private String truncateContent(String content) {
        if (content == null) {
            return "";
        }
        if (content.length() <= CONTENT_MAX_LENGTH) {
            return content;
        }
        return content.substring(0, CONTENT_MAX_LENGTH) + "...";
    }

    static String formatCategory(ReportCategory category) {
        return switch (category) {
            case PUB_INFO -> "펍 정보";
            case APP_ERROR -> "앱 오류";
            case OTHER -> "기타";
        };
    }
}
