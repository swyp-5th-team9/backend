package com.swift.sportspub.auth.dto.provider;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "카카오 사용자 정보 응답")
public record KakaoUserInfo(

        @Schema(description = "카카오 회원 고유 ID")
        Long id,

        @JsonProperty("kakao_account")
        @Schema(description = "카카오 계정 정보")
        KakaoAccount kakaoAccount
) {

    public String oauthId() {
        return String.valueOf(id);
    }

    public String nickname() {
        if (kakaoAccount == null || kakaoAccount.profile() == null) {
            return null;
        }
        return kakaoAccount.profile().nickname();
    }

    public record KakaoAccount(

            @Schema(description = "카카오 프로필 정보")
            Profile profile,

            @Schema(description = "카카오 계정 이메일")
            String email
    ) {
    }

    public record Profile(

            @Schema(description = "카카오 닉네임")
            String nickname
    ) {
    }
}
