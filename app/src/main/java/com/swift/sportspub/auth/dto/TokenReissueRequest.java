package com.swift.sportspub.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "토큰 재발급 요청")
public record TokenReissueRequest(

        @Schema(description = "로그인 또는 이전 재발급 시 발급받은 refresh token")
        @NotBlank(message = "refreshToken은 필수입니다.")
        String refreshToken
) {
}
