package com.swift.sportspub.auth;

import com.swift.sportspub.common.exception.BusinessException;
import com.swift.sportspub.common.exception.ErrorCode;

public final class BearerTokenExtractor {

    private static final String BEARER_PREFIX = "Bearer ";

    private BearerTokenExtractor() {
    }

    public static String extractAccessToken(String authorizationHeader) {
        if (authorizationHeader == null || authorizationHeader.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "Authorization 헤더는 필수입니다.");
        }

        String token = authorizationHeader.startsWith(BEARER_PREFIX)
                ? authorizationHeader.substring(BEARER_PREFIX.length())
                : authorizationHeader;

        if (token.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "accessToken은 필수입니다.");
        }

        return token.trim();
    }
}
