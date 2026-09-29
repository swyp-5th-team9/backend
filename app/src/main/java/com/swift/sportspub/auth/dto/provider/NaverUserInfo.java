package com.swift.sportspub.auth.dto.provider;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "네이버 사용자 정보 응답")
public record NaverUserInfo(

        @Schema(description = "네이버 사용자 정보 본문")
        NaverAccount response
) {

    public String oauthId() {
        if (response == null) {
            return null;
        }
        return response.id();
    }

    public String nickname() {
        if (response == null) {
            return null;
        }
        return response.nickname();
    }

    public record NaverAccount(

            @Schema(description = "네이버 회원 고유 ID")
            String id,

            @Schema(description = "네이버 닉네임")
            String nickname,

            @Schema(description = "네이버 계정 이메일")
            String email
    ) {
    }
}
