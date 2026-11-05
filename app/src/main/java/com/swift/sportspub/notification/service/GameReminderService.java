package com.swift.sportspub.notification.service;

import com.swift.sportspub.match.entity.Match;
import com.swift.sportspub.match.entity.MatchStatus;
import com.swift.sportspub.match.repository.MatchRepository;
import com.swift.sportspub.notification.entity.DeepLinkType;
import com.swift.sportspub.notification.entity.Notification;
import com.swift.sportspub.notification.entity.NotificationType;
import com.swift.sportspub.notification.repository.NotificationRepository;
import com.swift.sportspub.user.entity.User;
import com.swift.sportspub.user.repository.UserFavoriteTeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GameReminderService {

    private final MatchRepository matchRepository;
    private final UserFavoriteTeamRepository userFavoriteTeamRepository;
    private final NotificationRepository notificationRepository;

    @Transactional
    public int createDailyReminders(LocalDate today) {
        int created = 0;
        created += createReminders(today, DeepLinkType.TODAY_PUBS, "오늘");
        created += createReminders(today.plusDays(1), DeepLinkType.TOMORROW_GAME, "내일");
        return created;
    }

    private int createReminders(LocalDate matchDate, DeepLinkType deepLinkType, String dayWord) {
        List<Match> matches = matchRepository.findNotCanceledByMatchDate(matchDate, MatchStatus.CANCELED);
        int created = 0;

        for (Match match : matches) {
            List<User> users = userFavoriteTeamRepository.findActiveUsersSupportingTeams(
                    match.getHomeTeam().getTeamId(),
                    match.getAwayTeam().getTeamId()
            );
            for (User user : users) {
                if (notificationRepository.existsByUserUserIdAndMatchMatchIdAndDeepLinkType(
                        user.getUserId(),
                        match.getMatchId(),
                        deepLinkType
                )) {
                    continue;
                }

                notificationRepository.save(Notification.builder()
                        .user(user)
                        .match(match)
                        .type(NotificationType.GAME_REMINDER)
                        .title(dayWord + " 경기 있어요!")
                        .content(content(match, dayWord))
                        .deepLinkType(deepLinkType)
                        .build());
                created++;
            }
        }

        return created;
    }

    private String content(Match match, String dayWord) {
        String teams = match.getHomeTeam().getName() + "와 " + match.getAwayTeam().getName();
        if (match.getStartTime() == null) {
            return teams + " 경기가 " + dayWord + " 진행돼요.";
        }
        return teams + " 경기가 " + dayWord + " " + formatTime(match.getStartTime()) + "에 진행돼요.";
    }

    private String formatTime(LocalTime time) {
        int hour = time.getHour();
        String period = hour < 12 ? "오전" : "오후";
        int displayHour = hour % 12 == 0 ? 12 : hour % 12;
        if (time.getMinute() == 0) {
            return period + " " + displayHour + "시";
        }
        return period + " " + displayHour + "시 " + time.getMinute() + "분";
    }
}
