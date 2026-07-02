package com.swift.sportspub.user.service;

import com.swift.sportspub.auth.repository.RefreshTokenRepository;
import com.swift.sportspub.common.exception.BusinessException;
import com.swift.sportspub.common.exception.ErrorCode;
import com.swift.sportspub.team.entity.SportType;
import com.swift.sportspub.team.entity.Team;
import com.swift.sportspub.team.repository.TeamRepository;
import com.swift.sportspub.user.dto.OnboardingRequest;
import com.swift.sportspub.user.entity.OAuthProvider;
import com.swift.sportspub.user.entity.User;
import com.swift.sportspub.user.entity.UserFavoriteTeam;
import com.swift.sportspub.user.repository.UserFavoriteTeamRepository;
import com.swift.sportspub.user.repository.UserRepository;
import com.swift.sportspub.user.repository.WithdrawalReasonRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserFavoriteTeamRepository userFavoriteTeamRepository;

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private WithdrawalReasonRepository withdrawalReasonRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void onboarding_whenAlreadyCompleted_throwsConflict() {
        User user = User.createOAuthUser(OAuthProvider.KAKAO, "oauth-1");
        ReflectionTestUtils.setField(user, "userId", 1L);
        user.completeOnboarding("기존닉네임");

        given(userRepository.findActiveById(1L)).willReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.onboarding(1L, new OnboardingRequest("새닉네임", List.of())))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> {
                    BusinessException businessException = (BusinessException) ex;
                    assertThat(businessException.getErrorCode()).isEqualTo(ErrorCode.CONFLICT);
                    assertThat(businessException.getMessage()).isEqualTo("이미 온보딩이 완료된 사용자입니다.");
                });

        verify(userFavoriteTeamRepository, never()).deleteByUserId(1L);
    }

    @Test
    void getMyInfo_returnsFavoriteTeams() {
        User user = User.createOAuthUser(OAuthProvider.KAKAO, "oauth-1");
        ReflectionTestUtils.setField(user, "userId", 1L);
        user.completeOnboarding("닉네임");

        Team team = Team.builder()
                .sportType(SportType.KBO)
                .name("두산 베어스")
                .shortName("두산")
                .build();
        ReflectionTestUtils.setField(team, "teamId", 99L);

        UserFavoriteTeam favoriteTeam = UserFavoriteTeam.builder()
                .user(user)
                .team(team)
                .build();

        given(userRepository.findActiveById(1L)).willReturn(Optional.of(user));
        given(userFavoriteTeamRepository.findByUserUserIdOrderByCreatedAtAsc(1L))
                .willReturn(List.of(favoriteTeam));

        var response = userService.getMyInfo(1L);

        assertThat(response.favoriteTeams()).hasSize(1);
        assertThat(response.favoriteTeams().getFirst().teamId()).isEqualTo(99L);
        assertThat(response.favoriteTeams().getFirst().teamName()).isEqualTo("두산");
    }
}
