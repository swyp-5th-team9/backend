package com.swift.sportspub.auth.dto;

import com.swift.sportspub.user.entity.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "로그인 응답")
public record LoginResponse(

        @Schema(description = "백엔드에서 발급한 JWT access token")
        String accessToken,

        @Schema(description = "백엔드에서 발급한 JWT refresh token")
        String refreshToken,

        @Schema(description = "회원 역할", example = "FAN")
        UserRole role,

        @Schema(description = "온보딩 완료 여부", example = "false")
        boolean onboardingCompleted,

        @Schema(
                description = "탈퇴 후 보관 기간 이내 동일 OAuth 재로그인으로 계정이 복구되었으면 true",
                example = "false"
        )
        boolean restored
) {
}
