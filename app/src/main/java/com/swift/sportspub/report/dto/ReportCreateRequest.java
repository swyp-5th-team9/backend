package com.swift.sportspub.report.dto;

import com.swift.sportspub.report.entity.ReportCategory;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 * 제보 생성 요청 (multipart/form-data).
 *
 * <p>images 필드·multipart 바인딩·Swagger {@code @ArraySchema} 설정은 API 계약상 유지한다.
 */
@Getter
@Setter
@NoArgsConstructor
@Schema(description = "제보 생성 요청 (multipart/form-data)")
public class ReportCreateRequest {

    @NotNull(message = "제보 유형을 선택해주세요.")
    @Schema(description = "제보 유형 (PUB_INFO, APP_ERROR, OTHER)", example = "PUB_INFO", requiredMode = Schema.RequiredMode.REQUIRED)
    private ReportCategory category;

    @Schema(description = "펍 ID (선택, 홈/펍 상세에서 제보 시)", example = "12")
    private Long pubId;

    @NotBlank(message = "제보 내용을 입력해주세요.")
    @Size(max = 500, message = "제보 내용은 500자까지 입력할 수 있습니다.")
    @Schema(description = "제보 내용 (최대 500자)", example = "주소가 실제와 다릅니다.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String content;

    /**
     * 첨부 이미지 (최대 3장, 선택, 파일당 10MB 이하).
     * multipart part name: images. 검증은 ReportService에서 수행한다.
     */
    @ArraySchema(
            schema = @Schema(type = "string", format = "binary"),
            maxItems = 3
    )
    @Schema(description = "첨부 이미지 (최대 3장, 선택)")
    private List<MultipartFile> images = new ArrayList<>();
}
