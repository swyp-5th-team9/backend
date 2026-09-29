package com.swift.sportspub.report.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ReportErrorCode {

    INVALID_REPORT_CATEGORY(HttpStatus.BAD_REQUEST, "잘못된 제보 유형입니다."),
    REPORT_CONTENT_REQUIRED(HttpStatus.BAD_REQUEST, "제보 내용을 입력해주세요."),
    REPORT_CONTENT_TOO_LONG(HttpStatus.BAD_REQUEST, "제보 내용 500자 초과"),
    REPORT_IMAGE_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "첨부 이미지 최대 개수(3장) 초과"),
    REPORT_IMAGE_SIZE_EXCEEDED(HttpStatus.BAD_REQUEST, "첨부 이미지 용량(10MB) 초과"),
    UNSUPPORTED_IMAGE_FORMAT(HttpStatus.BAD_REQUEST, "지원하지 않는 이미지 형식"),
    PUB_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 펍"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String message;

    public String getCode() {
        if (this == INTERNAL_SERVER_ERROR) {
            return "INTERNAL_SERVER_ERROR";
        }
        return name();
    }
}
