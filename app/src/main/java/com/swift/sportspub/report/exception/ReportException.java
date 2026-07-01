package com.swift.sportspub.report.exception;

import lombok.Getter;

@Getter
public class ReportException extends RuntimeException {

    private final ReportErrorCode errorCode;

    public ReportException(ReportErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ReportException(ReportErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
