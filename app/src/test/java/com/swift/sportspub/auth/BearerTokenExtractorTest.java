package com.swift.sportspub.auth;

import com.swift.sportspub.common.exception.BusinessException;
import com.swift.sportspub.common.exception.ErrorCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BearerTokenExtractorTest {

    @Test
    void extractAccessToken_stripsBearerPrefix() {
        assertThat(BearerTokenExtractor.extractAccessToken("Bearer kakao-token"))
                .isEqualTo("kakao-token");
    }

    @Test
    void extractAccessToken_acceptsRawToken() {
        assertThat(BearerTokenExtractor.extractAccessToken("naver-token"))
                .isEqualTo("naver-token");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "Bearer ", "Bearer   "})
    void extractAccessToken_rejectsBlankHeader(String authorizationHeader) {
        assertThatThrownBy(() -> BearerTokenExtractor.extractAccessToken(authorizationHeader))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> {
                    BusinessException businessException = (BusinessException) ex;
                    assertThat(businessException.getErrorCode()).isEqualTo(ErrorCode.INVALID_INPUT);
                });
    }
}
