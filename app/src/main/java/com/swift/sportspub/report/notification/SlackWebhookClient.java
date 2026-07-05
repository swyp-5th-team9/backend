package com.swift.sportspub.report.notification;

import com.swift.sportspub.config.SlackProperties;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

/**
 * Slack Incoming Webhook HTTP 호출.
 *
 * <p>Webhook URL·JSON payload 전송을 담당한다. 실패 시 예외를 호출부로 전달한다.
 */
@Component
public class SlackWebhookClient {

    private final RestClient restClient;
    private final SlackProperties slackProperties;

    public SlackWebhookClient(RestClient.Builder restClientBuilder, SlackProperties slackProperties) {
        this.restClient = restClientBuilder.build();
        this.slackProperties = slackProperties;
    }

    public void sendText(String text) {
        restClient.post()
                .uri(slackProperties.getReportWebhookUrl())
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("text", text))
                .retrieve()
                .toBodilessEntity();
    }
}
