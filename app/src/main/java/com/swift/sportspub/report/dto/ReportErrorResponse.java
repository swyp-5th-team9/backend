package com.swift.sportspub.report.dto;

import com.swift.sportspub.report.exception.ReportErrorCode;

public record ReportErrorResponse(
        boolean success,
        String errorCode,
        String message
) {
    public static ReportErrorResponse of(ReportErrorCode errorCode) {
        return new ReportErrorResponse(false, errorCode.getCode(), errorCode.getMessage());
    }

    public static ReportErrorResponse of(ReportErrorCode errorCode, String message) {
        return new ReportErrorResponse(false, errorCode.getCode(), message);
    }
}
