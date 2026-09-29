package com.swift.sportspub.notification.repository;

import com.swift.sportspub.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query("""
            SELECT n FROM Notification n
            JOIN FETCH n.match m
            JOIN FETCH m.homeTeam
            JOIN FETCH m.awayTeam
            WHERE n.user.userId = :userId
            ORDER BY n.createdAt DESC, n.notificationId DESC
            """)
    List<Notification> findByUserUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);

    Optional<Notification> findByNotificationIdAndUserUserId(Long notificationId, Long userId);
}
