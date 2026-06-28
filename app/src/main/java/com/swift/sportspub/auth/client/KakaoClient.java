package com.swift.sportspub.auth.client;

import com.swift.sportspub.auth.dto.provider.KakaoUserInfo;
import com.swift.sportspub.auth.support.OAuthAuthorizationHeader;
import com.swift.sportspub.common.exception.BusinessException;
import com.swift.sportspub.common.exception.ErrorCode;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class KakaoClient {

    private static final String KAKAO_API_BASE_URL = "https://kapi.kakao.com";
    private static final String USER_INFO_PATH = "/v2/user/me";
    private static final String INVALID_TOKEN_MESSAGE = "유효하지 않은 카카오 accessToken입니다.";

    private final RestClient restClient;

    public KakaoClient(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder
                .baseUrl(KAKAO_API_BASE_URL)
                .build();
    }

    /*
     * 소셜 플랫폼 API 호출 책임을 Client 계층으로 분리한다.
     *
     * AuthService는 로그인 비즈니스 로직(회원 조회, 회원 생성, JWT 발급)에만 집중하고,
     * 카카오 API 호출 방식, URL, 헤더, 응답 검증은 KakaoClient가 담당한다.
     *
     * 이렇게 분리하면 향후 Google, Apple 로그인 추가 시 AuthService의 흐름은 유지한 채
     * 플랫폼별 Client만 추가하거나 교체할 수 있어 SRP와 유지보수성이 좋아진다.
     */
    public KakaoUserInfo getUserInfo(String accessToken) {
        try {
            KakaoUserInfo userInfo = restClient.get()
                    .uri(USER_INFO_PATH)
                    .header(HttpHeaders.AUTHORIZATION, OAuthAuthorizationHeader.toBearerAuthorization(accessToken))
                    .retrieve()
                    .body(KakaoUserInfo.class);

            if (userInfo == null || userInfo.id() == null) {
                throw new BusinessException(ErrorCode.UNAUTHORIZED, INVALID_TOKEN_MESSAGE);
            }

            return userInfo;
        } catch (RestClientException e) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, INVALID_TOKEN_MESSAGE);
        }
    }
}
