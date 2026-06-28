package com.swift.sportspub.auth.support;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OAuthAuthorizationHeaderTest {

    @Test
    void resolveAccessToken_returnsRawTokenWhenBearerPrefixMissing() {
        assertThat(OAuthAuthorizationHeader.resolveAccessToken("kakao-token"))
                .isEqualTo("kakao-token");
    }

    @Test
    void resolveAccessToken_stripsBearerPrefix() {
        assertThat(OAuthAuthorizationHeader.resolveAccessToken("Bearer kakao-token"))
                .isEqualTo("kakao-token");
    }

    @Test
    void toBearerAuthorization_avoidsDoubleBearerPrefix() {
        assertThat(OAuthAuthorizationHeader.toBearerAuthorization("Bearer kakao-token"))
                .isEqualTo("Bearer kakao-token");
    }
}
