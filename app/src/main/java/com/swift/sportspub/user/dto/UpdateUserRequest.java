package com.swift.sportspub.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 회원 정보 수정 요청 (multipart/form-data).
 */
@Getter
@Setter
@NoArgsConstructor
@Schema(description = "회원 정보 수정 요청 (multipart/form-data)")
public class UpdateUserRequest {

    @Schema(description = "사용자 닉네임 (2~20자). 미전달 시 기존 닉네임을 유지합니다.", example = "주양2")
    @Size(min = 2, max = 20, message = "nickname은 2~20자까지 입력할 수 있습니다.")
    private String nickname;

    @Schema(description = "선호 구단 ID 목록. multipart에서 teamIds=1&teamIds=3 형태로 반복 전달. 미전달 시 유지, 빈 값 없이 전달 시 전체 해제. 최대 3개.")
    @Size(max = 3, message = "teamIds는 최대 3개까지 선택할 수 있습니다.")
    private List<Long> teamIds;

    /**
     * 프로필 이미지 (선택). multipart part name: profileImage.
     */
    @Schema(type = "string", format = "binary", description = "프로필 이미지 (선택)")
    private MultipartFile profileImage;
}
