package com.swift.sportspub.report.exception;

import com.swift.sportspub.report.controller.ReportController;
import com.swift.sportspub.report.dto.ReportErrorResponse;
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
    public ResponseEntity<ReportErrorResponse> handleReport(ReportException e) {
        log.warn("Report exception: {}", e.getMessage());
        return ResponseEntity
                .status(e.getErrorCode().getStatus())
                .body(ReportErrorResponse.of(e.getErrorCode(), e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ReportErrorResponse> handleValidation(MethodArgumentNotValidException e) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        if (fieldError == null) {
            return ResponseEntity
                    .badRequest()
                    .body(ReportErrorResponse.of(ReportErrorCode.INVALID_REPORT_CATEGORY));
        }

        ReportErrorCode errorCode = mapValidationError(fieldError);
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ReportErrorResponse.of(errorCode, fieldError.getDefaultMessage()));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ReportErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        if (e.getRequiredType() == ReportCategory.class) {
            return ResponseEntity
                    .badRequest()
                    .body(ReportErrorResponse.of(ReportErrorCode.INVALID_REPORT_CATEGORY));
        }

        return ResponseEntity
                .badRequest()
                .body(ReportErrorResponse.of(ReportErrorCode.INVALID_REPORT_CATEGORY));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ReportErrorResponse> handleMaxUploadSize(MaxUploadSizeExceededException e) {
        log.warn("Report image size exceeded servlet limit: {}", e.getMessage());
        return ResponseEntity
                .badRequest()
                .body(ReportErrorResponse.of(ReportErrorCode.REPORT_IMAGE_SIZE_EXCEEDED));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ReportErrorResponse> handleUnknown(Exception e) {
        log.error("Unhandled report API exception", e);
        return ResponseEntity
                .status(ReportErrorCode.INTERNAL_SERVER_ERROR.getStatus())
                .body(ReportErrorResponse.of(ReportErrorCode.INTERNAL_SERVER_ERROR));
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
