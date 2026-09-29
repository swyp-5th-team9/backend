package com.swift.sportspub.notification.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.swift.sportspub.match.entity.Match;
import com.swift.sportspub.notification.entity.DeepLinkType;
import com.swift.sportspub.notification.entity.Notification;
import com.swift.sportspub.notification.entity.NotificationType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;

@Schema(description = "알림 목록 항목")
public record NotificationResponse(

        @Schema(description = "알림 ID", example = "1")
        Long notificationId,

        @Schema(description = "알림 대상 경기 ID", example = "120")
        Long matchId,

        @Schema(description = "경기 홈팀·원정팀 ID. DB에 저장하지 않고 matchId로 파생한다.", example = "[1, 2]")
        List<Long> teamIds,

        @Schema(description = "알림 유형", example = "GAME_REMINDER")
        NotificationType type,

        @Schema(description = "알림 제목", example = "오늘 경기 있어요!")
        String title,

        @Schema(description = "알림 내용", example = "LG 트윈스와 두산 베어스 경기가 오늘 오후 6시에 진행돼요.")
        String content,

        @Schema(description = "알림 발송 시각 (KST, UTC+09:00)", example = "2026-08-24T11:30:00+09:00")
        OffsetDateTime createdAt,

        @JsonProperty("isRead")
        @Schema(description = "알림 읽음 여부", example = "false")
        boolean isRead,

        @Schema(description = "알림 클릭 시 이동할 화면 유형", example = "TODAY_PUBS")
        DeepLinkType deepLinkType
) {
    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    public static NotificationResponse from(Notification notification) {
        Match match = notification.getMatch();
        return new NotificationResponse(
                notification.getNotificationId(),
                match.getMatchId(),
                List.of(match.getHomeTeam().getTeamId(), match.getAwayTeam().getTeamId()),
                notification.getType(),
                notification.getTitle(),
                notification.getContent(),
                notification.getCreatedAt().atZone(KST).toOffsetDateTime(),
                notification.isRead(),
                notification.getDeepLinkType()
        );
    }
}
