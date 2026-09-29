package com.swift.sportspub.notification.service;

import com.swift.sportspub.common.exception.BusinessException;
import com.swift.sportspub.common.exception.ErrorCode;
import com.swift.sportspub.match.entity.Match;
import com.swift.sportspub.notification.dto.NotificationResponse;
import com.swift.sportspub.notification.entity.DeepLinkType;
import com.swift.sportspub.notification.entity.Notification;
import com.swift.sportspub.notification.entity.NotificationType;
import com.swift.sportspub.notification.repository.NotificationRepository;
import com.swift.sportspub.team.entity.SportType;
import com.swift.sportspub.team.entity.Team;
import com.swift.sportspub.user.entity.OAuthProvider;
import com.swift.sportspub.user.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.BeanUtils;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationService notificationService;

    @Test
    void getMyNotifications_whenEmpty_returnsEmptyList() {
        given(notificationRepository.findByUserUserIdOrderByCreatedAtDesc(1L))
                .willReturn(List.of());

        List<NotificationResponse> responses = notificationService.getMyNotifications(1L);

        assertThat(responses).isEmpty();
        verify(notificationRepository).findByUserUserIdOrderByCreatedAtDesc(1L);
    }

    @Test
    void getMyNotifications_mapsMatchTeamsAndKstOffset() {
        Notification notification = notification(
                1L,
                120L,
                1L,
                2L,
                LocalDateTime.of(2026, 8, 24, 11, 30)
        );

        given(notificationRepository.findByUserUserIdOrderByCreatedAtDesc(1L))
                .willReturn(List.of(notification));

        List<NotificationResponse> responses = notificationService.getMyNotifications(1L);

        assertThat(responses).hasSize(1);
        NotificationResponse response = responses.get(0);
        assertThat(response.notificationId()).isEqualTo(1L);
        assertThat(response.matchId()).isEqualTo(120L);
        assertThat(response.teamIds()).containsExactly(1L, 2L);
        assertThat(response.type()).isEqualTo(NotificationType.GAME_REMINDER);
        assertThat(response.title()).isEqualTo("오늘 경기 있어요!");
        assertThat(response.isRead()).isFalse();
        assertThat(response.deepLinkType()).isEqualTo(DeepLinkType.TODAY_PUBS);
        assertThat(response.createdAt()).hasToString("2026-08-24T11:30+09:00");
    }

    @Test
    void readNotification_marksOwnedNotificationAsRead() {
        Notification notification = notification(
                1L, 120L, 1L, 2L, LocalDateTime.of(2026, 8, 24, 11, 30)
        );
        given(notificationRepository.findByNotificationIdAndUserUserId(1L, 10L))
                .willReturn(Optional.of(notification));

        notificationService.readNotification(10L, 1L);

        assertThat(notification.isRead()).isTrue();
    }

    @Test
    void readNotification_whenAlreadyRead_succeedsAndKeepsReadState() {
        Notification notification = notification(
                1L, 120L, 1L, 2L, LocalDateTime.of(2026, 8, 24, 11, 30)
        );
        notification.markAsRead();
        given(notificationRepository.findByNotificationIdAndUserUserId(1L, 10L))
                .willReturn(Optional.of(notification));

        notificationService.readNotification(10L, 1L);

        assertThat(notification.isRead()).isTrue();
    }

    @Test
    void readNotification_whenMissingOrNotOwned_throwsNotFound() {
        given(notificationRepository.findByNotificationIdAndUserUserId(99L, 10L))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> notificationService.readNotification(10L, 99L))
                .isInstanceOf(BusinessException.class)
                .satisfies(exception -> {
                    BusinessException businessException = (BusinessException) exception;
                    assertThat(businessException.getErrorCode()).isEqualTo(ErrorCode.NOT_FOUND);
                    assertThat(businessException.getMessage())
                            .isEqualTo("존재하지 않거나 처리할 수 없는 알림입니다.");
                });
    }

    @Test
    void deleteNotification_whenOwned_deletesNotification() {
        Notification notification = notification(
                1L, 120L, 1L, 2L, LocalDateTime.of(2026, 8, 24, 11, 30)
        );
        ReflectionTestUtils.setField(notification.getUser(), "userId", 10L);
        given(notificationRepository.findById(1L)).willReturn(Optional.of(notification));

        notificationService.deleteNotification(10L, 1L);

        verify(notificationRepository).delete(notification);
    }

    @Test
    void deleteNotification_whenMissing_throwsNotFound() {
        given(notificationRepository.findById(99L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> notificationService.deleteNotification(10L, 99L))
                .isInstanceOf(BusinessException.class)
                .satisfies(exception -> {
                    BusinessException businessException = (BusinessException) exception;
                    assertThat(businessException.getErrorCode()).isEqualTo(ErrorCode.NOT_FOUND);
                    assertThat(businessException.getMessage()).isEqualTo("알림을 찾을 수 없습니다.");
                });
    }

    @Test
    void deleteNotification_whenNotOwned_throwsAccessDenied() {
        Notification notification = notification(
                1L, 120L, 1L, 2L, LocalDateTime.of(2026, 8, 24, 11, 30)
        );
        ReflectionTestUtils.setField(notification.getUser(), "userId", 20L);
        given(notificationRepository.findById(1L)).willReturn(Optional.of(notification));

        assertThatThrownBy(() -> notificationService.deleteNotification(10L, 1L))
                .isInstanceOf(BusinessException.class)
                .satisfies(exception -> assertThat(
                        ((BusinessException) exception).getErrorCode()
                ).isEqualTo(ErrorCode.NOTIFICATION_ACCESS_DENIED));
    }

    private Notification notification(
            Long notificationId,
            Long matchId,
            Long homeTeamId,
            Long awayTeamId,
            LocalDateTime createdAt
    ) {
        Team home = Team.builder()
                .sportType(SportType.KBO)
                .name("LG 트윈스")
                .shortName("LG")
                .build();
        ReflectionTestUtils.setField(home, "teamId", homeTeamId);

        Team away = Team.builder()
                .sportType(SportType.KBO)
                .name("두산 베어스")
                .shortName("두산")
                .build();
        ReflectionTestUtils.setField(away, "teamId", awayTeamId);

        Match match = BeanUtils.instantiateClass(Match.class);
        ReflectionTestUtils.setField(match, "matchId", matchId);
        ReflectionTestUtils.setField(match, "homeTeam", home);
        ReflectionTestUtils.setField(match, "awayTeam", away);

        User user = User.builder()
                .oauthProvider(OAuthProvider.KAKAO)
                .oauthId("oauth-1")
                .build();

        Notification notification = Notification.builder()
                .user(user)
                .match(match)
                .type(NotificationType.GAME_REMINDER)
                .title("오늘 경기 있어요!")
                .content("LG 트윈스와 두산 베어스 경기가 오늘 오후 6시에 진행돼요.")
                .deepLinkType(DeepLinkType.TODAY_PUBS)
                .build();
        ReflectionTestUtils.setField(notification, "notificationId", notificationId);
        ReflectionTestUtils.setField(notification, "createdAt", createdAt);
        return notification;
    }
}
