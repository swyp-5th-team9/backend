package com.swift.sportspub.report.controller;

import com.swift.sportspub.common.response.ApiResponse;
import com.swift.sportspub.common.swagger.ApiFailResponse;
import com.swift.sportspub.common.swagger.DocResponse;
import com.swift.sportspub.common.swagger.DocResponses;
import com.swift.sportspub.report.dto.ReportCreateRequest;
import com.swift.sportspub.report.dto.ReportCreateResponse;
import com.swift.sportspub.report.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
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
            @DocResponse(
                    responseCode = "201",
                    description = "등록 성공",
                    content = @Content(schema = @Schema(implementation = ReportCreateResponse.class))
            ),
            @DocResponse(
                    responseCode = "400",
                    description = "ReportErrorCode — category/content 검증, 이미지 개수·용량·형식 오류",
                    content = @Content(
                            schema = @Schema(implementation = ApiFailResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "INVALID_REPORT_CATEGORY",
                                            value = "{\"success\":false,\"errorCode\":\"INVALID_REPORT_CATEGORY\",\"message\":\"제보 유형을 선택해주세요.\"}"
                                    ),
                                    @ExampleObject(
                                            name = "REPORT_CONTENT_REQUIRED",
                                            value = "{\"success\":false,\"errorCode\":\"REPORT_CONTENT_REQUIRED\",\"message\":\"제보 내용을 입력해주세요.\"}"
                                    ),
                                    @ExampleObject(
                                            name = "REPORT_CONTENT_TOO_LONG",
                                            value = "{\"success\":false,\"errorCode\":\"REPORT_CONTENT_TOO_LONG\",\"message\":\"제보 내용은 500자까지 입력할 수 있습니다.\"}"
                                    ),
                                    @ExampleObject(
                                            name = "REPORT_IMAGE_LIMIT_EXCEEDED",
                                            value = "{\"success\":false,\"errorCode\":\"REPORT_IMAGE_LIMIT_EXCEEDED\",\"message\":\"첨부 이미지 최대 개수(3장) 초과\"}"
                                    ),
                                    @ExampleObject(
                                            name = "REPORT_IMAGE_SIZE_EXCEEDED",
                                            value = "{\"success\":false,\"errorCode\":\"REPORT_IMAGE_SIZE_EXCEEDED\",\"message\":\"첨부 이미지 용량(10MB) 초과\"}"
                                    ),
                                    @ExampleObject(
                                            name = "UNSUPPORTED_IMAGE_FORMAT",
                                            value = "{\"success\":false,\"errorCode\":\"UNSUPPORTED_IMAGE_FORMAT\",\"message\":\"지원하지 않는 이미지 형식\"}"
                                    )
                            }
                    )
            ),
            @DocResponse(
                    responseCode = "401",
                    description = "UNAUTHORIZED — 인증 필요",
                    content = @Content(
                            schema = @Schema(implementation = ApiFailResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"success\":false,\"errorCode\":\"UNAUTHORIZED\",\"message\":\"인증이 필요합니다.\"}"
                            )
                    )
            ),
            @DocResponse(
                    responseCode = "404",
                    description = "PUB_NOT_FOUND 또는 NOT_FOUND — 존재하지 않는 펍·사용자",
                    content = @Content(
                            schema = @Schema(implementation = ApiFailResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "PUB_NOT_FOUND",
                                            value = "{\"success\":false,\"errorCode\":\"PUB_NOT_FOUND\",\"message\":\"존재하지 않는 펍\"}"
                                    ),
                                    @ExampleObject(
                                            name = "NOT_FOUND",
                                            value = "{\"success\":false,\"errorCode\":\"NOT_FOUND\",\"message\":\"사용자를 찾을 수 없습니다.\"}"
                                    )
                            }
                    )
            ),
            @DocResponse(
                    responseCode = "500",
                    description = "INTERNAL_SERVER_ERROR",
                    content = @Content(
                            schema = @Schema(implementation = ApiFailResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"success\":false,\"errorCode\":\"INTERNAL_SERVER_ERROR\",\"message\":\"서버 내부 오류가 발생했습니다.\"}"
                            )
                    )
            )
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
