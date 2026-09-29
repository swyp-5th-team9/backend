package com.swift.sportspub.report.exception;

import com.swift.sportspub.common.exception.BusinessException;
import com.swift.sportspub.common.response.ApiResponse;
import com.swift.sportspub.report.controller.ReportController;
import com.swift.sportspub.report.entity.ReportCategory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice(assignableTypes = ReportController.class)
public class ReportExceptionHandler {

    @ExceptionHandler(ReportException.class)
    public ResponseEntity<ApiResponse<Void>> handleReport(ReportException e) {
        log.warn("Report exception: {}", e.getMessage());
        return ResponseEntity
                .status(e.getErrorCode().getStatus())
                .body(toFailResponse(e.getErrorCode(), e.getMessage()));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusiness(BusinessException e) {
        log.warn("Report business exception: {}", e.getMessage());
        return ResponseEntity
                .status(e.getErrorCode().getStatus())
                .body(ApiResponse.fail(e.getErrorCode(), e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException e) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        if (fieldError == null) {
            return ResponseEntity
                    .badRequest()
                    .body(toFailResponse(ReportErrorCode.INVALID_REPORT_CATEGORY));
        }

        ReportErrorCode errorCode = mapValidationError(fieldError);
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(toFailResponse(errorCode, fieldError.getDefaultMessage()));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        if (e.getRequiredType() == ReportCategory.class) {
            return ResponseEntity
                    .badRequest()
                    .body(toFailResponse(ReportErrorCode.INVALID_REPORT_CATEGORY));
        }

        return ResponseEntity
                .badRequest()
                .body(toFailResponse(ReportErrorCode.INVALID_REPORT_CATEGORY));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiResponse<Void>> handleMaxUploadSize(MaxUploadSizeExceededException e) {
        log.warn("Report image size exceeded servlet limit: {}", e.getMessage());
        return ResponseEntity
                .badRequest()
                .body(toFailResponse(ReportErrorCode.REPORT_IMAGE_SIZE_EXCEEDED));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUnknown(Exception e) {
        log.error("Unhandled report API exception", e);
        return ResponseEntity
                .status(ReportErrorCode.INTERNAL_SERVER_ERROR.getStatus())
                .body(toFailResponse(ReportErrorCode.INTERNAL_SERVER_ERROR));
    }

    private ApiResponse<Void> toFailResponse(ReportErrorCode errorCode) {
        return ApiResponse.fail(errorCode.getCode(), errorCode.getMessage());
    }

    private ApiResponse<Void> toFailResponse(ReportErrorCode errorCode, String message) {
        return ApiResponse.fail(errorCode.getCode(), message);
    }

    private ReportErrorCode mapValidationError(FieldError fieldError) {
        return switch (fieldError.getField()) {
            case "category" -> ReportErrorCode.INVALID_REPORT_CATEGORY;
            case "content" -> mapContentValidationError(fieldError);
            default -> ReportErrorCode.INVALID_REPORT_CATEGORY;
        };
    }

    private ReportErrorCode mapContentValidationError(FieldError fieldError) {
        if ("Size".equals(fieldError.getCode())) {
            return ReportErrorCode.REPORT_CONTENT_TOO_LONG;
        }
        return ReportErrorCode.REPORT_CONTENT_REQUIRED;
    }
}
