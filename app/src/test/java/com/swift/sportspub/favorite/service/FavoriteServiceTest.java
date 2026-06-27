package com.swift.sportspub.favorite.service;

import com.swift.sportspub.common.exception.BusinessException;
import com.swift.sportspub.common.exception.ErrorCode;
import com.swift.sportspub.favorite.entity.Favorite;
import com.swift.sportspub.favorite.repository.FavoriteRepository;
import com.swift.sportspub.user.entity.OAuthProvider;
import com.swift.sportspub.user.entity.User;
import com.swift.sportspub.user.exception.UserNotFoundException;
import com.swift.sportspub.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class FavoriteServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private FavoriteRepository favoriteRepository;

    @InjectMocks
    private FavoriteService favoriteService;

    @Test
    void addFavorite_success() {
        User user = User.builder()
                .oauthProvider(OAuthProvider.KAKAO)
                .oauthId("oauth-1")
                .build();
        given(userService.getUser(1L)).willReturn(user);
        given(favoriteRepository.existsActivePub(10L)).willReturn(true);
        given(favoriteRepository.countByUserUserId(1L)).willReturn(0L);
        given(favoriteRepository.existsByUserUserIdAndPubId(1L, 10L)).willReturn(false);

        favoriteService.addFavorite(1L, 10L);

        ArgumentCaptor<Favorite> captor = ArgumentCaptor.forClass(Favorite.class);
        verify(favoriteRepository).save(captor.capture());
        assertThat(captor.getValue().getPubId()).isEqualTo(10L);
        assertThat(captor.getValue().getUser()).isEqualTo(user);
    }

    @Test
    void addFavorite_userNotFound() {
        given(userService.getUser(1L)).willThrow(new UserNotFoundException());

        assertThatThrownBy(() -> favoriteService.addFavorite(1L, 10L))
                .isInstanceOf(UserNotFoundException.class);

        verify(favoriteRepository, never()).save(any());
    }

    @Test
    void addFavorite_pubNotFound() {
        given(userService.getUser(1L)).willReturn(User.builder()
                .oauthProvider(OAuthProvider.KAKAO)
                .oauthId("oauth-1")
                .build());
        given(favoriteRepository.existsActivePub(10L)).willReturn(false);

        assertThatThrownBy(() -> favoriteService.addFavorite(1L, 10L))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> {
                    BusinessException businessException = (BusinessException) ex;
                    assertThat(businessException.getErrorCode()).isEqualTo(ErrorCode.INVALID_INPUT);
                    assertThat(businessException.getMessage()).contains("존재하지 않는 pubId");
                });

        verify(favoriteRepository, never()).save(any());
    }

    @Test
    void addFavorite_duplicate() {
        given(userService.getUser(1L)).willReturn(User.builder()
                .oauthProvider(OAuthProvider.KAKAO)
                .oauthId("oauth-1")
                .build());
        given(favoriteRepository.existsActivePub(10L)).willReturn(true);
        given(favoriteRepository.countByUserUserId(1L)).willReturn(1L);
        given(favoriteRepository.existsByUserUserIdAndPubId(1L, 10L)).willReturn(true);

        assertThatThrownBy(() -> favoriteService.addFavorite(1L, 10L))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> {
                    BusinessException businessException = (BusinessException) ex;
                    assertThat(businessException.getErrorCode()).isEqualTo(ErrorCode.CONFLICT);
                });

        verify(favoriteRepository, never()).save(any());
    }

    @Test
    void addFavorite_exceedsLimit() {
        given(userService.getUser(1L)).willReturn(User.builder()
                .oauthProvider(OAuthProvider.KAKAO)
                .oauthId("oauth-1")
                .build());
        given(favoriteRepository.existsActivePub(10L)).willReturn(true);
        given(favoriteRepository.countByUserUserId(1L)).willReturn(30L);

        assertThatThrownBy(() -> favoriteService.addFavorite(1L, 10L))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> {
                    BusinessException businessException = (BusinessException) ex;
                    assertThat(businessException.getErrorCode()).isEqualTo(ErrorCode.INVALID_INPUT);
                    assertThat(businessException.getMessage()).contains("최대 30개");
                });

        verify(favoriteRepository, never()).save(any());
    }
}
