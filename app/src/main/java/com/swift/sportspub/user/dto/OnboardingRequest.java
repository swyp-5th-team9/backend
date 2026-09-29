package com.swift.sportspub.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

@Schema(description = "회원 온보딩 요청")
public record OnboardingRequest(

        @Schema(description = "사용자 닉네임 (2~20자)", example = "주양")
        @NotBlank(message = "nickname은 필수입니다.")
        @Size(min = 2, max = 20, message = "nickname은 2~20자까지 입력할 수 있습니다.")
        String nickname,

        @Schema(description = "선호 구단 ID 목록. 미전달 시 저장하지 않음, 빈 배열 전달 시 미선택. 최대 3개, 존재하지 않는 ID는 400.", example = "[1, 3, 7]")
        @Size(max = 3, message = "teamIds는 최대 3개까지 선택할 수 있습니다.")
        List<Long> teamIds
) {
}
