package com.swift.sportspub.common.swagger;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * report API HTTP 오류 응답 Swagger 문서.
 * body 스키마: {@link ApiFailResponse} ({@code errorCode} = ReportErrorCode name)
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@ApiResponses({
        @ApiResponse(
                responseCode = "400",
                description = "INVALID_REPORT_CATEGORY, REPORT_CONTENT_REQUIRED, REPORT_CONTENT_TOO_LONG, REPORT_IMAGE_LIMIT_EXCEEDED, REPORT_IMAGE_SIZE_EXCEEDED, UNSUPPORTED_IMAGE_FORMAT",
                content = @Content(schema = @Schema(implementation = ApiFailResponse.class))
        ),
        @ApiResponse(
                responseCode = "401",
                description = "UNAUTHORIZED",
                content = @Content(schema = @Schema(implementation = ApiFailResponse.class))
        ),
        @ApiResponse(
                responseCode = "404",
                description = "PUB_NOT_FOUND 또는 NOT_FOUND (BusinessException)",
                content = @Content(schema = @Schema(implementation = ApiFailResponse.class))
        ),
        @ApiResponse(
                responseCode = "500",
                description = "INTERNAL_SERVER_ERROR",
                content = @Content(schema = @Schema(implementation = ApiFailResponse.class))
        )
})
public @interface DocReportErrorResponses {
}
