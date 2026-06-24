package com.swift.sportspub.auth.service;

import com.swift.sportspub.auth.client.KakaoClient;
import com.swift.sportspub.auth.client.NaverClient;
import com.swift.sportspub.auth.dto.TokenResponse;
import com.swift.sportspub.auth.dto.LoginResponse;
import com.swift.sportspub.auth.entity.RefreshToken;
import com.swift.sportspub.auth.dto.provider.KakaoUserInfo;
import com.swift.sportspub.auth.dto.provider.NaverUserInfo;
import com.swift.sportspub.auth.jwt.JwtProvider;
import com.swift.sportspub.auth.repository.RefreshTokenRepository;
import com.swift.sportspub.common.exception.BusinessException;
import com.swift.sportspub.common.exception.ErrorCode;
import com.swift.sportspub.user.entity.OAuthProvider;
import com.swift.sportspub.user.entity.User;
import com.swift.sportspub.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HexFormat;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final String INVALID_REFRESH_TOKEN_MESSAGE = "유효하지 않은 refreshToken입니다.";

    private final KakaoClient kakaoClient;
    private final NaverClient naverClient;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProvider jwtProvider;

    @Value("${app.jwt.refresh-ttl-seconds}")
    private long refreshTtlSeconds;

    /*
     * 로그인 흐름은 Controller가 아니라 Service에서 처리한다.
     *
     * Controller는 HTTP 요청/응답 변환에 집중하고, AuthService는 로그인 비즈니스 로직
     * (소셜 사용자 조회, 회원 조회/생성, JWT 발급)을 하나의 트랜잭션 경계 안에서 담당한다.
     * 이렇게 분리하면 추후 Android 외 다른 클라이언트가 생겨도 동일한 로그인 정책을 재사용할 수 있다.
     */
    @Transactional
    public LoginResponse loginWithKakao(String accessToken) {
        KakaoUserInfo userInfo = kakaoClient.getUserInfo(accessToken);
        User user = findOrCreateUser(OAuthProvider.KAKAO, userInfo.oauthId());

        return createLoginResponse(user);
    }

    /*
     * RefreshToken 재발급은 Rotation 방식으로 처리한다.
     *
     * 요청으로 들어온 RefreshToken이 JWT로 유효하고, DB에 저장된 토큰 해시와 일치할 때만
     * 새 AccessToken과 RefreshToken을 발급한다. 재발급 성공 시 기존 RefreshToken은 더 이상
     * 사용할 수 없도록 삭제하고 새 RefreshToken만 저장해 탈취 토큰의 재사용 가능성을 줄인다.
     */
    @Transactional
    public TokenResponse reissue(String refreshToken) {
        validateRefreshToken(refreshToken);

        Long userId = jwtProvider.extractUserId(refreshToken);
        String refreshTokenHash = hashRefreshToken(refreshToken);
        RefreshToken storedRefreshToken = refreshTokenRepository.findByTokenHash(refreshTokenHash)
                .orElseThrow(() -> new BusinessException(ErrorCode.UNAUTHORIZED, INVALID_REFRESH_TOKEN_MESSAGE));

        if (!storedRefreshToken.getTokenHash().equals(refreshTokenHash)
                || !storedRefreshToken.getUser().getUserId().equals(userId)
                || storedRefreshToken.isExpired()) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, INVALID_REFRESH_TOKEN_MESSAGE);
        }

        return createTokenResponse(storedRefreshToken.getUser());
    }

    /*
     * 현재 정책은 사용자당 활성 RefreshToken 1개 유지이며, 로그아웃 시 서버가 인정하는 RefreshToken을 삭제한다.
     *
     * JwtAuthenticationFilter가 SecurityContext에 userId를 principal로 저장하므로,
     * Controller는 인증된 userId만 전달하고 AuthService가 해당 사용자의 RefreshToken 제거를 담당한다.
     */
    @Transactional
    public void logout(Long userId) {
        refreshTokenRepository.deleteByUserUserId(userId);
    }

    @Transactional
    public LoginResponse loginWithNaver(String accessToken) {
        NaverUserInfo userInfo = naverClient.getUserInfo(accessToken);
        User user = findOrCreateUser(OAuthProvider.NAVER, userInfo.oauthId());

        return createLoginResponse(user);
    }

    /*
     * KakaoClient/NaverClient는 외부 소셜 플랫폼 API 호출 책임을 가진다.
     *
     * AuthService가 HTTP URL, 인증 헤더, 플랫폼별 응답 구조를 직접 알게 되면
     * 로그인 정책과 외부 연동 세부사항이 섞인다. Client를 분리하면 AuthService는
     * "어떤 provider의 oauthId인가"만 다루고, 향후 Google/Apple 로그인 추가 시
     * 새 Client와 provider 분기만 추가하면 되어 수정 범위를 줄일 수 있다.
     */
    private User findOrCreateUser(OAuthProvider oauthProvider, String oauthId) {
        return userRepository.findByOauthProviderAndOauthId(oauthProvider, oauthId)
                .orElseGet(() -> createUser(oauthProvider, oauthId));
    }

    /*
     * 신규 소셜 회원은 서비스 기본 권한인 FAN으로 시작하고 온보딩은 미완료 상태로 둔다.
     *
     * 소셜 로그인 단계에서는 provider와 oauthId만 신뢰 가능한 식별 정보로 사용하고,
     * 닉네임과 응원 팀 같은 서비스 프로필은 별도 온보딩 API에서 사용자가 직접 선택하게 한다.
     */
    private User createUser(OAuthProvider oauthProvider, String oauthId) {
        User user = User.createOAuthUser(oauthProvider, oauthId);
        return userRepository.save(user);
    }

    /*
     * 로그인 성공 후 같은 User 식별자(userId)를 기준으로 AccessToken과 RefreshToken을 발급한다.
     *
     * AccessToken은 API 요청 인증에 사용되고, RefreshToken은 AccessToken 재발급을 위한 토큰이다.
     * RefreshToken은 서버 DB에 SHA-256 해시로 저장해 이후 재발급 요청이
     * "서버가 현재 인정하는 토큰"인지 확인한다.
     */
    private LoginResponse createLoginResponse(User user) {
        TokenResponse tokenResponse = createTokenResponse(user);

        return new LoginResponse(
                tokenResponse.accessToken(),
                tokenResponse.refreshToken(),
                user.getRole(),
                user.isOnboardingCompleted()
        );
    }

    private TokenResponse createTokenResponse(User user) {
        String accessToken = jwtProvider.createAccessToken(user.getUserId());
        String refreshToken = jwtProvider.createRefreshToken(user.getUserId());

        replaceRefreshToken(user, refreshToken);

        return new TokenResponse(accessToken, refreshToken);
    }

    /*
     * 현재 정책은 사용자당 RefreshToken 1개 유지다.
     *
     * 로그인 또는 재발급 시 기존 토큰을 남겨두면 여러 RefreshToken이 동시에 유효해져
     * 로그아웃, 탈취 대응, 토큰 회전 정책이 복잡해진다. 따라서 기존 토큰을 삭제하고
     * 새 토큰만 저장해 서버가 인정하는 RefreshToken을 하나로 제한한다.
     */
    private void replaceRefreshToken(User user, String refreshToken) {
        refreshTokenRepository.deleteByUserUserId(user.getUserId());
        refreshTokenRepository.save(RefreshToken.builder()
                .user(user)
                .tokenHash(hashRefreshToken(refreshToken))
                .expiresAt(LocalDateTime.now().plusSeconds(refreshTtlSeconds))
                .build());
    }

    /*
     * JWT 서명/만료 검증과 type=REFRESH 검증을 모두 수행한다.
     *
     * AccessToken도 JWT 구조상 유효할 수 있으므로, 재발급 API에서는 type claim을 확인해
     * RefreshToken만 사용할 수 있도록 제한한다.
     */
    private void validateRefreshToken(String refreshToken) {
        if (!jwtProvider.validateToken(refreshToken) || !jwtProvider.isRefreshToken(refreshToken)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, INVALID_REFRESH_TOKEN_MESSAGE);
        }
    }

    private String hashRefreshToken(String refreshToken) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] digest = messageDigest.digest(refreshToken.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm is not available.", e);
        }
    }
}
