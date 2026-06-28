package com.swift.sportspub.auth.support;

import org.springframework.util.StringUtils;

/**
 * 소셜 SDK Access Token용 Authorization 헤더 정규화.
 * <p>
 * 앱은 {@code Authorization} 헤더에 SDK 토큰만 보낼 수 있고, OkHttp Interceptor 등으로
 * {@code Bearer } 접두사가 붙을 수도 있다. 카카오/네이버 API는 {@code Bearer {token}} 형식을 요구하므로
 * 중복 {@code Bearer Bearer}를 방지하기 위해 접두사를 제거한 뒤 한 번만 붙인다.
 */
public final class OAuthAuthorizationHeader {

    private static final String BEARER_PREFIX = "Bearer ";

    private OAuthAuthorizationHeader() {
    }

    public static String resolveAccessToken(String authorization) {
        if (!StringUtils.hasText(authorization)) {
            return authorization;
        }

        String trimmed = authorization.trim();
        if (trimmed.regionMatches(true, 0, BEARER_PREFIX, 0, BEARER_PREFIX.length())) {
            return trimmed.substring(BEARER_PREFIX.length()).trim();
        }
        return trimmed;
    }

    public static String toBearerAuthorization(String authorization) {
        return BEARER_PREFIX + resolveAccessToken(authorization);
    }
}
