package com.swift.sportspub.common.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swift.sportspub.common.exception.ErrorCode;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ApiResponseTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void successResponse_serializesWithoutErrorFields() throws Exception {
        String json = objectMapper.writeValueAsString(ApiResponse.success("ok"));

        assertThat(json).contains("\"success\":true");
        assertThat(json).contains("\"data\":\"ok\"");
        assertThat(json).doesNotContain("errorCode");
        assertThat(json).doesNotContain("message");
    }

    @Test
    void failResponse_serializesErrorCodeAndMessage() throws Exception {
        String json = objectMapper.writeValueAsString(
                ApiResponse.fail(ErrorCode.NOT_FOUND, "사용자를 찾을 수 없습니다.")
        );

        assertThat(json).contains("\"success\":false");
        assertThat(json).contains("\"errorCode\":\"NOT_FOUND\"");
        assertThat(json).contains("\"message\":\"사용자를 찾을 수 없습니다.\"");
        assertThat(json).doesNotContain("\"data\"");
    }
}
