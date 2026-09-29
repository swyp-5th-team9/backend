package com.swift.sportspub.auth.service;

import com.swift.sportspub.auth.client.KakaoClient;
import com.swift.sportspub.auth.client.NaverClient;
import com.swift.sportspub.auth.dto.LoginResponse;
import com.swift.sportspub.auth.dto.provider.KakaoUserInfo;
import com.swift.sportspub.auth.jwt.JwtProvider;
import com.swift.sportspub.auth.repository.RefreshTokenRepository;
import com.swift.sportspub.user.config.UserWithdrawalProperties;
import com.swift.sportspub.user.entity.OAuthProvider;
import com.swift.sportspub.user.entity.User;
import com.swift.sportspub.user.entity.UserRole;
import com.swift.sportspub.user.repository.UserRepository;
import com.swift.sportspub.user.service.UserHardDeleteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    private static final String OAUTH_ID = "123";
    private static final String ACCESS_TOKEN = "kakao-access-token";

    @Mock
    private KakaoClient kakaoClient;

    @Mock
    private NaverClient naverClient;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private UserHardDeleteService userHardDeleteService;

    private final UserWithdrawalProperties userWithdrawalProperties = new UserWithdrawalProperties();

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authService, "userWithdrawalProperties", userWithdrawalProperties);
        ReflectionTestUtils.setField(authService, "refreshTtlSeconds", 1209600L);
        userWithdrawalProperties.setRetentionDays(30);

        when(jwtProvider.createAccessToken(any())).thenReturn("jwt-access");
        when(jwtProvider.createRefreshToken(any())).thenReturn("jwt-refresh");
        when(refreshTokenRepository.findByUserUserId(any())).thenReturn(Optional.empty());
        given(kakaoClient.getUserInfo(ACCESS_TOKEN))
                .willReturn(new KakaoUserInfo(123L, null));
    }

    @Test
    void loginWithKakao_whenUserNotFound_createsUserAndReturnsRestoredFalse() {
        given(userRepository.findByOauthProviderAndOauthIdIncludingDeleted(OAuthProvider.KAKAO, OAUTH_ID))
                .willReturn(Optional.empty());
        User newUser = User.createOAuthUser(OAuthProvider.KAKAO, OAUTH_ID);
        ReflectionTestUtils.setField(newUser, "userId", 10L);
        given(userRepository.save(any(User.class))).willReturn(newUser);

        LoginResponse response = authService.loginWithKakao(ACCESS_TOKEN);

        assertThat(response.restored()).isFalse();
        assertThat(response.onboardingCompleted()).isFalse();
        assertThat(response.role()).isEqualTo(UserRole.FAN);
        verify(userHardDeleteService, never()).hardDelete(any());
    }

    @Test
    void loginWithKakao_whenActiveUser_returnsRestoredFalse() {
        User activeUser = activeUser();
        given(userRepository.findByOauthProviderAndOauthIdIncludingDeleted(OAuthProvider.KAKAO, OAUTH_ID))
                .willReturn(Optional.of(activeUser));

        LoginResponse response = authService.loginWithKakao(ACCESS_TOKEN);

        assertThat(response.restored()).isFalse();
        assertThat(response.onboardingCompleted()).isTrue();
        verify(userHardDeleteService, never()).hardDelete(any());
    }

    @Test
    void loginWithKakao_whenWithdrawnWithinRetention_restoresUserAndReturnsRestoredTrue() {
        User withdrawnUser = withdrawnUser(LocalDateTime.now().minusDays(10));
        given(userRepository.findByOauthProviderAndOauthIdIncludingDeleted(OAuthProvider.KAKAO, OAUTH_ID))
                .willReturn(Optional.of(withdrawnUser));

        LoginResponse response = authService.loginWithKakao(ACCESS_TOKEN);

        assertThat(response.restored()).isTrue();
        assertThat(response.onboardingCompleted()).isTrue();
        assertThat(withdrawnUser.isDeleted()).isFalse();
        assertThat(withdrawnUser.getNickname()).isEqualTo("탈퇴닉네임");
        assertThat(withdrawnUser.getProfileImageUrl())
                .isEqualTo("https://bucket.s3.ap-northeast-2.amazonaws.com/profiles/old.jpg");
        verify(userHardDeleteService, never()).hardDelete(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void loginWithKakao_whenWithdrawnExpired_hardDeletesAndCreatesNewUser() {
        User expiredUser = withdrawnUser(LocalDateTime.now().minusDays(31));
        given(userRepository.findByOauthProviderAndOauthIdIncludingDeleted(OAuthProvider.KAKAO, OAUTH_ID))
                .willReturn(Optional.of(expiredUser));

        User newUser = User.createOAuthUser(OAuthProvider.KAKAO, OAUTH_ID);
        ReflectionTestUtils.setField(newUser, "userId", 99L);
        given(userRepository.save(any(User.class))).willReturn(newUser);

        LoginResponse response = authService.loginWithKakao(ACCESS_TOKEN);

        assertThat(response.restored()).isFalse();
        assertThat(response.onboardingCompleted()).isFalse();
        verify(userHardDeleteService).hardDelete(expiredUser);

        ArgumentCaptor<User> saveCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(saveCaptor.capture());
        assertThat(saveCaptor.getValue().getOauthId()).isEqualTo(OAUTH_ID);
    }

    private User activeUser() {
        User user = User.createOAuthUser(OAuthProvider.KAKAO, OAUTH_ID);
        ReflectionTestUtils.setField(user, "userId", 1L);
        user.completeOnboarding("닉네임");
        return user;
    }

    private User withdrawnUser(LocalDateTime deletedAt) {
        User user = User.builder()
                .oauthProvider(OAuthProvider.KAKAO)
                .oauthId(OAUTH_ID)
                .onboardingCompleted(true)
                .nickname("탈퇴닉네임")
                .deletedAt(deletedAt)
                .build();
        ReflectionTestUtils.setField(user, "userId", 1L);
        user.updateProfileImage("https://bucket.s3.ap-northeast-2.amazonaws.com/profiles/old.jpg");
        return user;
    }
}
