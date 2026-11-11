package com.swift.sportspub.notification.push;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MessagingErrorCode;
import com.google.firebase.messaging.Notification;
import com.swift.sportspub.config.FcmProperties;
import com.swift.sportspub.notification.entity.DeepLinkType;
import com.swift.sportspub.user.entity.UserFcmToken;
import com.swift.sportspub.user.repository.UserFcmTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * FCM 푸시 발송.
 *
 * <p>미설정({@code app.fcm.enabled=false} 또는 credentials 없음)이면 no-op.
 * 발송 실패는 알림 DB 저장을 롤백하지 않는다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FcmPushService {

    private static final String DATA_DEEP_LINK_TYPE = "deepLinkType";
    private static final String DATA_TEAM_IDS = "teamIds";
    private static final String DATA_TITLE = "title";
    private static final String DATA_BODY = "body";

    private final FcmProperties fcmProperties;
    private final UserFcmTokenRepository userFcmTokenRepository;
    private final ObjectProvider<FirebaseMessaging> firebaseMessaging;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void sendGameReminder(
            Long userId,
            String title,
            String body,
            DeepLinkType deepLinkType,
            List<Long> teamIds
    ) {
        if (!fcmProperties.isConfigured()) {
            return;
        }

        FirebaseMessaging messaging = firebaseMessaging.getIfAvailable();
        if (messaging == null) {
            log.warn("FCM enabled 이지만 FirebaseMessaging 빈이 없습니다. userId={}", userId);
            return;
        }

        List<UserFcmToken> tokens = userFcmTokenRepository.findAllByUserUserId(userId);
        if (tokens.isEmpty()) {
            log.debug("FCM 토큰 없음 — 푸시 스킵. userId={}", userId);
            return;
        }

        String teamIdsCsv = teamIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        for (UserFcmToken tokenEntity : tokens) {
            sendOne(messaging, tokenEntity.getToken(), title, body, deepLinkType.name(), teamIdsCsv);
        }
    }

    private void sendOne(
            FirebaseMessaging messaging,
            String token,
            String title,
            String body,
            String deepLinkType,
            String teamIdsCsv
    ) {
        Message message = Message.builder()
                .setToken(token)
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .putData(DATA_TITLE, title)
                .putData(DATA_BODY, body)
                .putData(DATA_DEEP_LINK_TYPE, deepLinkType)
                .putData(DATA_TEAM_IDS, teamIdsCsv)
                .build();

        try {
            messaging.send(message);
            log.debug("FCM 전송 성공 tokenSuffix=...{}", suffix(token));
        } catch (FirebaseMessagingException e) {
            if (isInvalidToken(e)) {
                log.info("무효 FCM 토큰 삭제 tokenSuffix=...{} code={}", suffix(token), e.getMessagingErrorCode());
                userFcmTokenRepository.deleteByToken(token);
                return;
            }
            log.warn("FCM 전송 실패 tokenSuffix=...{} code={}", suffix(token), e.getMessagingErrorCode(), e);
        } catch (Exception e) {
            log.warn("FCM 전송 중 예외 tokenSuffix=...{}", suffix(token), e);
        }
    }

    private boolean isInvalidToken(FirebaseMessagingException e) {
        return e.getMessagingErrorCode() == MessagingErrorCode.UNREGISTERED;
    }

    private String suffix(String token) {
        if (token == null || token.length() < 8) {
            return "****";
        }
        return token.substring(token.length() - 8);
    }
}
