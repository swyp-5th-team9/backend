package com.swift.sportspub.common.swagger;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * OpenAPI 문서용 공통 실패 응답 스키마.
 *
 * <p>실제 런타임 타입은 {@link com.swift.sportspub.common.response.ApiResponse}이며,
 * 실패 시 {@code data} 필드는 JSON에 포함되지 않는다({@code @JsonInclude(NON_NULL)}).
 */
@Schema(name = "ApiFailResponse", description = "공통 API 실패 응답")
public record ApiFailResponse(

        @Schema(description = "성공 여부", example = "false")
        boolean success,

        @Schema(
                description = "에러 코드. auth/user/favorite는 ErrorCode enum name, report는 ReportErrorCode enum name",
                example = "NOT_FOUND"
        )
        String errorCode,

        @Schema(description = "에러 메시지", example = "리소스를 찾을 수 없습니다.")
        String message
) {
}
