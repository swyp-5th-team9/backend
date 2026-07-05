package com.swift.sportspub.report.notification;

import com.swift.sportspub.config.SlackProperties;
import com.swift.sportspub.report.entity.Report;
import com.swift.sportspub.report.entity.ReportCategory;
import com.swift.sportspub.user.entity.OAuthProvider;
import com.swift.sportspub.user.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SlackNotificationServiceTest {

    @Mock
    private SlackProperties slackProperties;

    @Mock
    private SlackWebhookClient slackWebhookClient;

    @InjectMocks
    private SlackNotificationService slackNotificationService;

    @Test
    void notifyReportCreated_whenWebhookNotConfigured_doesNothing() {
        given(slackProperties.isReportWebhookConfigured()).willReturn(false);

        slackNotificationService.notifyReportCreated(sampleReport(), null, 0);

        verify(slackWebhookClient, never()).sendText(anyString());
    }

    @Test
    void notifyReportCreated_whenSendFails_doesNotThrow() {
        given(slackProperties.isReportWebhookConfigured()).willReturn(true);
        doThrow(new RuntimeException("slack down"))
                .when(slackWebhookClient).sendText(anyString());

        slackNotificationService.notifyReportCreated(sampleReport(), null, 1);
    }

    @Test
    void notifyReportCreated_whenConfigured_sendsMessage() {
        given(slackProperties.isReportWebhookConfigured()).willReturn(true);

        slackNotificationService.notifyReportCreated(sampleReport(), null, 2);

        verify(slackWebhookClient).sendText(anyString());
    }

    private Report sampleReport() {
        User user = User.builder()
                .oauthProvider(OAuthProvider.KAKAO)
                .oauthId("oauth-1")
                .build();
        Report report = Report.builder()
                .user(user)
                .category(ReportCategory.PUB_INFO)
                .content("테스트 제보 내용")
                .build();
        ReflectionTestUtils.setField(report, "reportId", 21L);
        ReflectionTestUtils.setField(report, "createdAt", LocalDateTime.of(2026, 7, 5, 11, 30));
        return report;
    }
}
