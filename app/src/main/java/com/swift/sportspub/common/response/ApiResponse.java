package com.swift.sportspub.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.swift.sportspub.common.exception.ErrorCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * 공통 API 응답.
 *
 * <p>성공: {@code { "success": true, "data": ... }}
 * <p>실패: {@code { "success": false, "errorCode": "...", "message": "..." }} (#60)
 */
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "공통 API 응답")
public class ApiResponse<T> {

    @Schema(description = "성공 여부", example = "true")
    private final boolean success;

    @Schema(description = "성공 시 payload")
    private final T data;

    @Schema(description = "실패 시 에러 코드 (ErrorCode 또는 ReportErrorCode name)", example = "NOT_FOUND")
    private final String errorCode;

    @Schema(description = "실패 시 메시지", example = "리소스를 찾을 수 없습니다.")
    private final String message;

    private ApiResponse(boolean success, T data, String errorCode, String message) {
        this.success = success;
        this.data = data;
        this.errorCode = errorCode;
        this.message = message;
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, null, null);
    }

    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(true, null, null, null);
    }

    public static <T> ApiResponse<T> fail(ErrorCode errorCode) {
        return new ApiResponse<>(false, null, errorCode.name(), errorCode.getDefaultMessage());
    }

    public static <T> ApiResponse<T> fail(ErrorCode errorCode, String message) {
        return new ApiResponse<>(false, null, errorCode.name(), message);
    }

    public static <T> ApiResponse<T> fail(String errorCode, String message) {
        return new ApiResponse<>(false, null, errorCode, message);
    }
}
