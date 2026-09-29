package com.swift.sportspub.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "토큰 응답")
public record TokenResponse(

        @Schema(description = "새로 발급한 JWT access token")
        String accessToken,

        @Schema(description = "새로 발급한 JWT refresh token")
        String refreshToken
) {
}
