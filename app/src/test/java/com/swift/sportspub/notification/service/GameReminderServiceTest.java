package com.swift.sportspub.notification.service;

import com.swift.sportspub.match.entity.Match;
import com.swift.sportspub.match.entity.MatchStatus;
import com.swift.sportspub.match.repository.MatchRepository;
import com.swift.sportspub.notification.entity.DeepLinkType;
import com.swift.sportspub.notification.entity.Notification;
import com.swift.sportspub.notification.entity.NotificationType;
import com.swift.sportspub.notification.push.FcmPushService;
import com.swift.sportspub.notification.repository.NotificationRepository;
import com.swift.sportspub.team.entity.SportType;
import com.swift.sportspub.team.entity.Team;
import com.swift.sportspub.user.entity.OAuthProvider;
import com.swift.sportspub.user.entity.User;
import com.swift.sportspub.user.repository.UserFavoriteTeamRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.BeanUtils;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class GameReminderServiceTest {

    @Mock
    private MatchRepository matchRepository;

    @Mock
    private UserFavoriteTeamRepository userFavoriteTeamRepository;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private FcmPushService fcmPushService;

    @InjectMocks
    private GameReminderService gameReminderService;

    @Test
    void createDailyReminders_createsTodayAndTomorrowRemindersForSupporters() {
        LocalDate today = LocalDate.of(2026, 9, 5);
        Match todayMatch = match(120L, 1L, "LG 트윈스", 2L, "두산 베어스", LocalTime.of(18, 0));
        Match tomorrowMatch = match(121L, 1L, "LG 트윈스", 3L, "KT 위즈", null);
        User user = user(10L);

        given(matchRepository.findNotCanceledByMatchDate(today, MatchStatus.CANCELED))
                .willReturn(List.of(todayMatch));
        given(matchRepository.findNotCanceledByMatchDate(today.plusDays(1), MatchStatus.CANCELED))
                .willReturn(List.of(tomorrowMatch));
        given(userFavoriteTeamRepository.findActiveUsersSupportingTeams(1L, 2L))
                .willReturn(List.of(user));
        given(userFavoriteTeamRepository.findActiveUsersSupportingTeams(1L, 3L))
                .willReturn(List.of(user));
        given(notificationRepository.existsByUserUserIdAndMatchMatchIdAndDeepLinkType(
                eq(10L), any(), any()
        )).willReturn(false);

        int created = gameReminderService.createDailyReminders(today);

        ArgumentCaptor<Notification> saved = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository, org.mockito.Mockito.times(2)).save(saved.capture());
        assertThat(created).isEqualTo(2);

        Notification todayNotification = saved.getAllValues().get(0);
        assertThat(todayNotification.getType()).isEqualTo(NotificationType.GAME_REMINDER);
        assertThat(todayNotification.getDeepLinkType()).isEqualTo(DeepLinkType.TODAY_PUBS);
        assertThat(todayNotification.getTitle()).isEqualTo("오늘 경기 있어요!");
        assertThat(todayNotification.getContent())
                .isEqualTo("LG 트윈스와 두산 베어스 경기가 오늘 오후 6시에 진행돼요.");
        assertThat(todayNotification.isRead()).isFalse();

        Notification tomorrowNotification = saved.getAllValues().get(1);
        assertThat(tomorrowNotification.getDeepLinkType()).isEqualTo(DeepLinkType.TOMORROW_GAME);
        assertThat(tomorrowNotification.getTitle()).isEqualTo("내일 경기 있어요!");
        assertThat(tomorrowNotification.getContent())
                .isEqualTo("LG 트윈스와 KT 위즈 경기가 내일 진행돼요.");

        verify(fcmPushService).sendGameReminder(
                10L,
                "오늘 경기 있어요!",
                "LG 트윈스와 두산 베어스 경기가 오늘 오후 6시에 진행돼요.",
                DeepLinkType.TODAY_PUBS,
                List.of(1L, 2L)
        );
        verify(fcmPushService).sendGameReminder(
                10L,
                "내일 경기 있어요!",
                "LG 트윈스와 KT 위즈 경기가 내일 진행돼요.",
                DeepLinkType.TOMORROW_GAME,
                List.of(1L, 3L)
        );
    }

    @Test
    void createDailyReminders_skipsExistingReminder() {
        LocalDate today = LocalDate.of(2026, 9, 5);
        Match todayMatch = match(120L, 1L, "LG 트윈스", 2L, "두산 베어스", LocalTime.of(18, 0));
        User user = user(10L);

        given(matchRepository.findNotCanceledByMatchDate(today, MatchStatus.CANCELED))
                .willReturn(List.of(todayMatch));
        given(matchRepository.findNotCanceledByMatchDate(today.plusDays(1), MatchStatus.CANCELED))
                .willReturn(List.of());
        given(userFavoriteTeamRepository.findActiveUsersSupportingTeams(1L, 2L))
                .willReturn(List.of(user));
        given(notificationRepository.existsByUserUserIdAndMatchMatchIdAndDeepLinkType(
                10L, 120L, DeepLinkType.TODAY_PUBS
        )).willReturn(true);

        int created = gameReminderService.createDailyReminders(today);

        assertThat(created).isZero();
        verify(notificationRepository, never()).save(any());
        verify(fcmPushService, never()).sendGameReminder(any(), any(), any(), any(), any());
    }

    private Match match(
            Long matchId,
            Long homeTeamId,
            String homeName,
            Long awayTeamId,
            String awayName,
            LocalTime startTime
    ) {
        Match match = BeanUtils.instantiateClass(Match.class);
        ReflectionTestUtils.setField(match, "matchId", matchId);
        ReflectionTestUtils.setField(match, "homeTeam", team(homeTeamId, homeName));
        ReflectionTestUtils.setField(match, "awayTeam", team(awayTeamId, awayName));
        ReflectionTestUtils.setField(match, "startTime", startTime);
        ReflectionTestUtils.setField(match, "status", MatchStatus.SCHEDULED);
        return match;
    }

    private Team team(Long teamId, String name) {
        Team team = Team.builder()
                .sportType(SportType.KBO)
                .name(name)
                .shortName(name)
                .build();
        ReflectionTestUtils.setField(team, "teamId", teamId);
        return team;
    }

    private User user(Long userId) {
        User user = User.builder()
                .oauthProvider(OAuthProvider.KAKAO)
                .oauthId("oauth-" + userId)
                .build();
        ReflectionTestUtils.setField(user, "userId", userId);
        return user;
    }
}
