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
import com.swift.sportspub.user.config.UserWithdrawalProperties;
import com.swift.sportspub.user.entity.OAuthProvider;
import com.swift.sportspub.user.entity.User;
import com.swift.sportspub.user.repository.UserFavoriteTeamRepository;
import com.swift.sportspub.user.repository.UserRepository;
import com.swift.sportspub.user.service.UserHardDeleteService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final String INVALID_REFRESH_TOKEN_MESSAGE = "유효하지 않은 refreshToken입니다.";

    private final KakaoClient kakaoClient;
    private final NaverClient naverClient;
    private final UserRepository userRepository;
    private final UserFavoriteTeamRepository userFavoriteTeamRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProvider jwtProvider;
    private final UserHardDeleteService userHardDeleteService;
    private final UserWithdrawalProperties userWithdrawalProperties;

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
        UserLoginOutcome outcome = findOrCreateUser(OAuthProvider.KAKAO, userInfo.oauthId());

        return createLoginResponse(outcome);
    }

    /*
     * RefreshToken 재발급은 Rotation 방식으로 처리한다.
     *
     * 요청으로 들어온 RefreshToken이 JWT로 유효하고, DB에 저장된 토큰 해시와 일치할 때만
     * 새 AccessToken과 RefreshToken을 발급한다. 재발급 성공 시 기존 RefreshToken row의
     * token_hash를 갱신(UPDATE)해 이전 refreshToken은 더 이상 사용할 수 없게 한다.
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

        User user = userRepository.findActiveById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.UNAUTHORIZED, INVALID_REFRESH_TOKEN_MESSAGE));

        return createTokenResponse(user, storedRefreshToken);
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
        UserLoginOutcome outcome = findOrCreateUser(OAuthProvider.NAVER, userInfo.oauthId());

        return createLoginResponse(outcome);
    }

    /*
     * KakaoClient/NaverClient는 외부 소셜 플랫폼 API 호출 책임을 가진다.
     *
     * AuthService가 HTTP URL, 인증 헤더, 플랫폼별 응답 구조를 직접 알게 되면
     * 로그인 정책과 외부 연동 세부사항이 섞인다. Client를 분리하면 AuthService는
     * "어떤 provider의 oauthId인가"만 다루고, 향후 Google/Apple 로그인 추가 시
     * 새 Client와 provider 분기만 추가하면 되어 수정 범위를 줄일 수 있다.
     */
    /*
     * OAuth 로그인 흐름:
     * 1) 탈퇴 포함 조회 2) 없으면 신규 3) 활성이면 기존 로그인
     * 4) 탈퇴·보관 기간 이내면 복구(restored=true) 5) 기간 초과면 Hard Delete 후 신규(restored=false)
     */
    private UserLoginOutcome findOrCreateUser(OAuthProvider oauthProvider, String oauthId) {
        Optional<User> optionalUser = userRepository.findByOauthProviderAndOauthIdIncludingDeleted(
                oauthProvider,
                oauthId
        );

        if (optionalUser.isEmpty()) {
            return new UserLoginOutcome(createUser(oauthProvider, oauthId), false);
        }

        User user = optionalUser.get();
        if (!user.isDeleted()) {
            return new UserLoginOutcome(user, false);
        }

        LocalDateTime now = LocalDateTime.now();
        int retentionDays = userWithdrawalProperties.getRetentionDays();

        if (user.canRestore(now, retentionDays)) {
            restoreWithdrawnUser(user);
            return new UserLoginOutcome(user, true);
        }

        userHardDeleteService.hardDelete(user);
        return new UserLoginOutcome(createUser(oauthProvider, oauthId), false);
    }

    /*
     * restoreForReLogin(): deletedAt 해제, nickname null, onboardingCompleted false
     * 선호 구단은 별도 삭제 — 탈퇴 전 프로필을 그대로 두지 않는다.
     */
    private void restoreWithdrawnUser(User user) {
        user.restoreForReLogin();
        userFavoriteTeamRepository.deleteByUserId(user.getUserId());
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
    private LoginResponse createLoginResponse(UserLoginOutcome outcome) {
        TokenResponse tokenResponse = createTokenResponse(outcome.user());

        return new LoginResponse(
                tokenResponse.accessToken(),
                tokenResponse.refreshToken(),
                outcome.user().getRole(),
                outcome.user().isOnboardingCompleted(),
                outcome.restored()
        );
    }

    private record UserLoginOutcome(User user, boolean restored) {
    }

    private TokenResponse createTokenResponse(User user) {
        return createTokenResponse(user, null);
    }

    private TokenResponse createTokenResponse(User user, RefreshToken existingRefreshToken) {
        String accessToken = jwtProvider.createAccessToken(user.getUserId());
        String refreshToken = jwtProvider.createRefreshToken(user.getUserId());

        replaceRefreshToken(user, refreshToken, existingRefreshToken);

        return new TokenResponse(accessToken, refreshToken);
    }

    /*
     * 사용자당 RefreshToken row는 uk_refresh_tokens_user 로 1개만 허용된다.
     *
     * delete + insert 방식은 reissue 시 DELETE SQL 없이 INSERT만 실행되어
     * 동일 user_id에 duplicate key(uk_refresh_tokens_user)가 발생했다.
     * 기존 row가 있으면 rotate()로 필드를 변경하고, 트랜잭션 커밋 시 JPA Dirty Checking이
     * UPDATE SQL을 생성한다(명시적 save 없이도 영속 상태 엔티티 변경이 반영됨).
     * INSERT는 최초 생성 시에만 수행한다.
     * reissue()에서 이미 조회한 existingRefreshToken을 넘기면 user_id 기준 재조회를 생략한다.
     */
    private void replaceRefreshToken(User user, String refreshToken, RefreshToken existingRefreshToken) {
        String tokenHash = hashRefreshToken(refreshToken);
        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(refreshTtlSeconds);

        if (existingRefreshToken != null) {
            /*
             * reissue()에서 findByTokenHash로 조회한 영속 엔티티이므로 rotate() 후
             * 트랜잭션 커밋 시 Dirty Checking으로 UPDATE가 실행된다.
             */
            existingRefreshToken.rotate(tokenHash, expiresAt);
            return;
        }

        refreshTokenRepository.findByUserUserId(user.getUserId())
                .ifPresentOrElse(
                        stored -> stored.rotate(tokenHash, expiresAt),
                        () -> refreshTokenRepository.save(RefreshToken.builder()
                                .user(user)
                                .tokenHash(tokenHash)
                                .expiresAt(expiresAt)
                                .build())
                );
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
