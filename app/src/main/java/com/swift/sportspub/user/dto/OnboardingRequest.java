package com.swift.sportspub.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

@Schema(description = "회원 온보딩 요청")
public record OnboardingRequest(

        @Schema(description = "사용자 닉네임", example = "주양")
        @NotBlank(message = "nickname은 필수입니다.")
        @Size(max = 20, message = "nickname은 최대 20자까지 입력할 수 있습니다.")
        String nickname,

        @Schema(description = "선호 구단 ID 목록. 현재는 저장하지 않고 최대 개수만 검증합니다.", example = "[1, 3, 7]")
        @Size(max = 3, message = "teamIds는 최대 3개까지 선택할 수 있습니다.")
        List<Long> teamIds
) {
}
