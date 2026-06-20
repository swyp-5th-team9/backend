package com.swift.sportspub.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "소셜 로그인 요청")
public record SocialLoginRequest(

        @Schema(description = "Android SDK 로그인 후 발급받은 소셜 access token", example = "social_access_token")
        @NotBlank(message = "accessToken은 필수입니다.")
        String accessToken
) {
}
