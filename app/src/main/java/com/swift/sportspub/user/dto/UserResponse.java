package com.swift.sportspub.user.dto;

import com.swift.sportspub.user.entity.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "회원 정보 응답")
public record UserResponse(

        @Schema(description = "회원 ID", example = "1")
        Long userId,

        @Schema(description = "사용자 닉네임", example = "주양")
        String nickname,

        @Schema(description = "회원 역할", example = "FAN")
        UserRole role,

        @Schema(description = "온보딩 완료 여부", example = "true")
        boolean onboardingCompleted,

        @Schema(description = "선호 구단 목록")
        List<FavoriteTeamResponse> favoriteTeams
) {
}
