package com.swift.sportspub.auth.service;

import com.swift.sportspub.auth.client.KakaoClient;
import com.swift.sportspub.auth.client.NaverClient;
import com.swift.sportspub.auth.dto.LoginResponse;
import com.swift.sportspub.auth.dto.provider.KakaoUserInfo;
import com.swift.sportspub.auth.dto.provider.NaverUserInfo;
import com.swift.sportspub.auth.jwt.JwtProvider;
import com.swift.sportspub.user.entity.OAuthProvider;
import com.swift.sportspub.user.entity.User;
import com.swift.sportspub.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final KakaoClient kakaoClient;
    private final NaverClient naverClient;
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

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
     * 현재 단계에서는 RefreshToken 저장소가 없으므로 발급만 수행하며, 저장/회전/로그아웃 정책은
     * 별도 기능에서 확장한다.
     */
    private LoginResponse createLoginResponse(User user) {
        String accessToken = jwtProvider.createAccessToken(user.getUserId());
        String refreshToken = jwtProvider.createRefreshToken(user.getUserId());

        return new LoginResponse(
                accessToken,
                refreshToken,
                user.getRole(),
                user.isOnboardingCompleted()
        );
    }
}
