package com.swift.sportspub.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

import java.util.List;

@Schema(
        description = "회원 정보 수정 요청",
        example = "{\"nickname\":\"주양2\",\"teamIds\":[1,3,7]}"
)
public record UpdateUserRequest(

        @Schema(description = "사용자 닉네임. 미전달 시 기존 닉네임을 유지합니다.", example = "주양2")
        @Size(max = 20, message = "nickname은 최대 20자까지 입력할 수 있습니다.")
        String nickname,

        @Schema(description = "선호 구단 ID 목록. 미전달 시 유지, 빈 배열 전달 시 전체 해제합니다.", example = "[1, 3, 7]")
        @Size(max = 3, message = "teamIds는 최대 3개까지 선택할 수 있습니다.")
        List<Long> teamIds
) {
}
