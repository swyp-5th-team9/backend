package com.swift.sportspub.report.controller;

import com.swift.sportspub.common.response.ApiResponse;
import com.swift.sportspub.common.swagger.DocResponse;
import com.swift.sportspub.common.swagger.DocResponses;
import com.swift.sportspub.report.dto.ReportCreateRequest;
import com.swift.sportspub.report.dto.ReportCreateResponse;
import com.swift.sportspub.report.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
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

/**
 * 제보 API.
 *
 * <p>multipart/form-data, {@code @ModelAttribute}, API URL·응답 형식은 유지한다.
 * 이미지 업로드는 ReportService → ReportS3StorageService(S3 putObject)로 처리한다.
 */
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
                    category는 PUB_INFO(펍 정보), APP_ERROR(앱 오류), OTHER(기타) 중 하나이다.
                    content는 필수이며 최대 500자까지 입력할 수 있다.
                    pubId는 선택 값으로, 홈 또는 펍 상세에서 제보하는 경우에만 전달한다.
                    images는 선택 값이며 최대 3장, 파일당 10MB 이하만 첨부할 수 있다.
                    등록된 제보는 PENDING 상태로 저장되며, 등록 후 수정·삭제할 수 없다.
                    """,
            requestBody = @RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            schema = @Schema(implementation = ReportCreateRequest.class)
                    )
            )
    )
    @DocResponses({
            @DocResponse(responseCode = "201", description = "등록 성공"),
            @DocResponse(responseCode = "400", description = "INVALID_REPORT_CATEGORY, REPORT_CONTENT_REQUIRED, REPORT_CONTENT_TOO_LONG, REPORT_IMAGE_LIMIT_EXCEEDED, REPORT_IMAGE_SIZE_EXCEEDED, UNSUPPORTED_IMAGE_FORMAT"),
            @DocResponse(responseCode = "401", description = "UNAUTHORIZED"),
            @DocResponse(responseCode = "404", description = "PUB_NOT_FOUND"),
            @DocResponse(responseCode = "500", description = "INTERNAL_SERVER_ERROR")
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
