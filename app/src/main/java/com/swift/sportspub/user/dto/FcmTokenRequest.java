package com.swift.sportspub.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "FCM 디바이스 토큰 등록/갱신 요청")
public record FcmTokenRequest(

        @NotBlank(message = "token은 필수입니다.")
        @Size(max = 512, message = "token은 512자 이하여야 합니다.")
        @Schema(description = "FCM 디바이스 토큰", example = "dK8f...:APA91b...")
        String token
) {
}
