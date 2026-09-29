package com.swift.sportspub.notification.service;

import com.swift.sportspub.common.exception.BusinessException;
import com.swift.sportspub.common.exception.ErrorCode;
import com.swift.sportspub.notification.dto.NotificationResponse;
import com.swift.sportspub.notification.entity.Notification;
import com.swift.sportspub.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private static final String NOTIFICATION_NOT_FOUND_MESSAGE =
            "존재하지 않거나 처리할 수 없는 알림입니다.";

    private final NotificationRepository notificationRepository;

    @Transactional(readOnly = true)
    public List<NotificationResponse> getMyNotifications(Long userId) {
        return notificationRepository.findByUserUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(NotificationResponse::from)
                .toList();
    }

    @Transactional
    public void readNotification(Long userId, Long notificationId) {
        Notification notification = notificationRepository
                .findByNotificationIdAndUserUserId(notificationId, userId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.NOT_FOUND,
                        NOTIFICATION_NOT_FOUND_MESSAGE
                ));

        notification.markAsRead();
    }
}
