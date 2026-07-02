package com.swift.sportspub.favorite.service;

import com.swift.sportspub.common.exception.BusinessException;
import com.swift.sportspub.common.exception.ErrorCode;
import com.swift.sportspub.favorite.dto.FavoriteListResponse;
import com.swift.sportspub.favorite.entity.Favorite;
import com.swift.sportspub.favorite.repository.FavoriteRepository;
import com.swift.sportspub.pub.entity.Pub;
import com.swift.sportspub.pub.entity.PubImage;
import com.swift.sportspub.pub.entity.PubStatus;
import com.swift.sportspub.pub.entity.Region;
import com.swift.sportspub.pub.entity.SubRegion;
import com.swift.sportspub.pub.repository.PubImageRepository;
import com.swift.sportspub.pub.repository.PubRepository;
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
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;

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

    @Mock
    private PubRepository pubRepository;

    @Mock
    private PubImageRepository pubImageRepository;

    @InjectMocks
    private FavoriteService favoriteService;

    @Test
    void getMyFavorites_returnsPubDetails() {
        User user = User.builder()
                .oauthProvider(OAuthProvider.KAKAO)
                .oauthId("oauth-1")
                .build();
        Pub pub = Pub.builder()
                .name("시그니처 펍")
                .address("서울 마포구")
                .region(Region.MAPO)
                .latitude(new BigDecimal("37.5563000"))
                .longitude(new BigDecimal("126.9226000"))
                .status(PubStatus.OPEN)
                .build();
        ReflectionTestUtils.setField(pub, "pubId", 12L);
        ReflectionTestUtils.setField(pub, "subRegion", SubRegion.HONGDAE_HAPJEONG);

        Favorite favorite = Favorite.builder()
                .user(user)
                .pub(pub)
                .build();
        ReflectionTestUtils.setField(favorite, "favoriteId", 1L);
        ReflectionTestUtils.setField(favorite, "pubId", 12L);

        PubImage image = PubImage.builder()
                .pubId(12L)
                .imageUrl("https://example.com/pub1.jpg")
                .displayOrder((short) 0)
                .build();

        given(favoriteRepository.findTop30ByUserUserIdOrderByCreatedAtDesc(1L))
                .willReturn(List.of(favorite));
        given(pubRepository.findAllById(List.of(12L))).willReturn(List.of(pub));
        given(pubImageRepository.findAllByPubIdInOrderByPubIdAscDisplayOrderAsc(List.of(12L)))
                .willReturn(List.of(image));

        FavoriteListResponse response = favoriteService.getMyFavorites(1L);

        assertThat(response.favorites()).hasSize(1);
        assertThat(response.favorites().get(0).favoriteId()).isEqualTo(1L);
        assertThat(response.favorites().get(0).pubId()).isEqualTo(12L);
        assertThat(response.favorites().get(0).pubName()).isEqualTo("시그니처 펍");
        assertThat(response.favorites().get(0).region()).isEqualTo("홍대/합정");
        assertThat(response.favorites().get(0).thumbnailImageUrl()).isEqualTo("https://example.com/pub1.jpg");
    }

    @Test
    void addFavorite_success() {
        User user = User.builder()
                .oauthProvider(OAuthProvider.KAKAO)
                .oauthId("oauth-1")
                .build();
        Pub pub = Pub.builder()
                .name("테스트 펍")
                .address("서울")
                .region(Region.MAPO)
                .latitude(new BigDecimal("37.5563000"))
                .longitude(new BigDecimal("126.9226000"))
                .status(PubStatus.OPEN)
                .build();
        ReflectionTestUtils.setField(pub, "pubId", 10L);

        given(userService.getUser(1L)).willReturn(user);
        given(pubRepository.findById(10L)).willReturn(java.util.Optional.of(pub));
        given(favoriteRepository.existsByUserUserIdAndPubPubId(1L, 10L)).willReturn(false);
        given(favoriteRepository.countByUserUserId(1L)).willReturn(0L);
        Favorite saved = Favorite.builder()
                .user(user)
                .pub(pub)
                .build();
        ReflectionTestUtils.setField(saved, "favoriteId", 42L);
        given(favoriteRepository.save(any())).willReturn(saved);

        Long favoriteId = favoriteService.addFavorite(1L, 10L);

        assertThat(favoriteId).isEqualTo(42L);

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
        given(pubRepository.findById(10L)).willReturn(java.util.Optional.empty());

        assertThatThrownBy(() -> favoriteService.addFavorite(1L, 10L))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> {
                    BusinessException businessException = (BusinessException) ex;
                    assertThat(businessException.getErrorCode()).isEqualTo(ErrorCode.NOT_FOUND);
                });

        verify(favoriteRepository, never()).save(any());
    }

    @Test
    void addFavorite_duplicate() {
        Pub pub = Pub.builder()
                .name("테스트 펍")
                .address("서울")
                .region(Region.MAPO)
                .latitude(new BigDecimal("37.5563000"))
                .longitude(new BigDecimal("126.9226000"))
                .status(PubStatus.OPEN)
                .build();
        ReflectionTestUtils.setField(pub, "pubId", 10L);

        given(userService.getUser(1L)).willReturn(User.builder()
                .oauthProvider(OAuthProvider.KAKAO)
                .oauthId("oauth-1")
                .build());
        given(pubRepository.findById(10L)).willReturn(java.util.Optional.of(pub));
        given(favoriteRepository.existsByUserUserIdAndPubPubId(1L, 10L)).willReturn(true);

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
        Pub pub = Pub.builder()
                .name("테스트 펍")
                .address("서울")
                .region(Region.MAPO)
                .latitude(new BigDecimal("37.5563000"))
                .longitude(new BigDecimal("126.9226000"))
                .status(PubStatus.OPEN)
                .build();
        ReflectionTestUtils.setField(pub, "pubId", 10L);

        given(userService.getUser(1L)).willReturn(User.builder()
                .oauthProvider(OAuthProvider.KAKAO)
                .oauthId("oauth-1")
                .build());
        given(pubRepository.findById(10L)).willReturn(java.util.Optional.of(pub));
        given(favoriteRepository.existsByUserUserIdAndPubPubId(1L, 10L)).willReturn(false);
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
