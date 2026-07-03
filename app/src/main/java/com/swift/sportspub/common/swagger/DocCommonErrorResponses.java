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
 * auth / user / favorite 공통 HTTP 오류 응답 Swagger 문서.
 * body 스키마: {@link ApiFailResponse} ({@code errorCode} = {@link com.swift.sportspub.common.exception.ErrorCode} name)
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@ApiResponses({
        @ApiResponse(
                responseCode = "400",
                description = "INVALID_INPUT",
                content = @Content(schema = @Schema(implementation = ApiFailResponse.class))
        ),
        @ApiResponse(
                responseCode = "401",
                description = "UNAUTHORIZED",
                content = @Content(schema = @Schema(implementation = ApiFailResponse.class))
        ),
        @ApiResponse(
                responseCode = "404",
                description = "NOT_FOUND",
                content = @Content(schema = @Schema(implementation = ApiFailResponse.class))
        ),
        @ApiResponse(
                responseCode = "409",
                description = "CONFLICT",
                content = @Content(schema = @Schema(implementation = ApiFailResponse.class))
        ),
        @ApiResponse(
                responseCode = "500",
                description = "INTERNAL_ERROR",
                content = @Content(schema = @Schema(implementation = ApiFailResponse.class))
        )
})
public @interface DocCommonErrorResponses {
}
