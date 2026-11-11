package com.swift.sportspub.user.service;

import com.swift.sportspub.user.dto.FcmTokenRequest;
import com.swift.sportspub.user.entity.OAuthProvider;
import com.swift.sportspub.user.entity.User;
import com.swift.sportspub.user.entity.UserFcmToken;
import com.swift.sportspub.user.exception.UserNotFoundException;
import com.swift.sportspub.user.repository.UserFcmTokenRepository;
import com.swift.sportspub.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class FcmTokenServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserFcmTokenRepository userFcmTokenRepository;

    @InjectMocks
    private FcmTokenService fcmTokenService;

    @Test
    void register_savesNewToken() {
        User user = user(10L);
        given(userRepository.findActiveById(10L)).willReturn(Optional.of(user));
        given(userFcmTokenRepository.findByToken("token-a")).willReturn(Optional.empty());

        fcmTokenService.register(10L, new FcmTokenRequest("token-a"));

        ArgumentCaptor<UserFcmToken> captor = ArgumentCaptor.forClass(UserFcmToken.class);
        verify(userFcmTokenRepository).save(captor.capture());
        assertThat(captor.getValue().getToken()).isEqualTo("token-a");
        assertThat(captor.getValue().getUser()).isEqualTo(user);
    }

    @Test
    void register_reassignsExistingTokenToCurrentUser() {
        User previousOwner = user(1L);
        User currentUser = user(10L);
        UserFcmToken existing = UserFcmToken.builder()
                .user(previousOwner)
                .token("token-a")
                .build();

        given(userRepository.findActiveById(10L)).willReturn(Optional.of(currentUser));
        given(userFcmTokenRepository.findByToken("token-a")).willReturn(Optional.of(existing));

        fcmTokenService.register(10L, new FcmTokenRequest("token-a"));

        assertThat(existing.getUser()).isEqualTo(currentUser);
        verify(userFcmTokenRepository, never()).save(any());
    }

    @Test
    void register_throwsWhenUserMissing() {
        given(userRepository.findActiveById(10L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> fcmTokenService.register(10L, new FcmTokenRequest("token-a")))
                .isInstanceOf(UserNotFoundException.class);
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
