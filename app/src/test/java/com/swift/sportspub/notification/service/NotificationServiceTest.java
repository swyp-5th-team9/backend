package com.swift.sportspub.notification.service;

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

import static org.assertj.core.api.Assertions.assertThat;
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
