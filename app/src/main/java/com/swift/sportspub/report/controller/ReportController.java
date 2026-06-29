package com.swift.sportspub.report.controller;

import com.swift.sportspub.common.response.ApiResponse;
import com.swift.sportspub.common.swagger.DocResponse;
import com.swift.sportspub.common.swagger.DocResponses;
import com.swift.sportspub.report.dto.ReportCreateRequest;
import com.swift.sportspub.report.dto.ReportCreateResponse;
import com.swift.sportspub.report.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Report", description = "제보 API")
@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @Operation(
            summary = "제보 등록",
            description = """
                    현재 로그인한 사용자가 앱 이용 중 발견한 문제를 제보한다.
                    category는 PUB_INFO, APP_ERROR, OTHER 중 하나이며 content는 필수이다.
                    pubId는 선택 값으로, 홈 또는 펍 상세에서 제보하는 경우에만 전달한다.
                    images는 선택 값이며 최대 3장까지 첨부할 수 있다.
                    등록된 제보는 PENDING 상태로 저장된다.
                    """
    )
    @DocResponses({
            @DocResponse(responseCode = "201", description = "등록 성공"),
            @DocResponse(responseCode = "400", description = "입력값 오류"),
            @DocResponse(responseCode = "401", description = "인증 필요"),
            @DocResponse(responseCode = "404", description = "존재하지 않는 펍"),
            @DocResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ReportCreateResponse>> createReport(
            @AuthenticationPrincipal Long userId,
            @Valid @ModelAttribute ReportCreateRequest request
    ) {
        ReportCreateResponse response = reportService.createReport(userId, request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response));
    }
}
