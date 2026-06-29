package com.swift.sportspub.report.dto;

import com.swift.sportspub.report.entity.ReportCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "제보 생성 요청 (multipart/form-data)")
public class ReportCreateRequest {

    @NotNull(message = "category는 필수입니다.")
    @Schema(description = "제보 카테고리", example = "PUB_INFO", requiredMode = Schema.RequiredMode.REQUIRED)
    private ReportCategory category;

    @Schema(description = "세부 카테고리 (선택)", example = "WRONG_ADDRESS")
    private String subcategory;

    @Schema(description = "펍 ID (선택)", example = "12")
    private Long pubId;

    @NotBlank(message = "content는 필수입니다.")
    @Schema(description = "제보 내용", example = "주소가 실제와 다릅니다.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String content;

    @Schema(description = "첨부 이미지 (최대 3장, 선택)")
    private List<MultipartFile> images = new ArrayList<>();
}
