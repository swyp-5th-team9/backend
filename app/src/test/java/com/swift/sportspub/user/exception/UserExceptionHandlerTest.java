package com.swift.sportspub.user.exception;

import com.swift.sportspub.common.exception.ErrorCode;
import com.swift.sportspub.common.response.ApiResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;

import static org.assertj.core.api.Assertions.assertThat;

class UserExceptionHandlerTest {

    private final UserExceptionHandler handler = new UserExceptionHandler();

    @Test
    void handleMaxUploadSize_returnsInvalidInput() {
        ResponseEntity<ApiResponse<Void>> response =
                handler.handleMaxUploadSize(new MaxUploadSizeExceededException(10L));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getErrorCode()).isEqualTo(ErrorCode.INVALID_INPUT.name());
        assertThat(response.getBody().getMessage()).isEqualTo("프로필 이미지 용량(10MB) 초과");
    }

    @Test
    void handleMultipart_returnsInvalidInput() {
        ResponseEntity<ApiResponse<Void>> response =
                handler.handleMultipart(new MultipartException("parse failed"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getErrorCode()).isEqualTo(ErrorCode.INVALID_INPUT.name());
        assertThat(response.getBody().getMessage()).isEqualTo("잘못된 multipart 요청입니다.");
    }
}
