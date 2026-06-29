package com.swift.sportspub.report.dto;

import com.swift.sportspub.report.entity.Report;
import com.swift.sportspub.report.entity.ReportStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "제보 생성 응답")
public record ReportCreateResponse(

        @Schema(description = "제보 ID", example = "1")
        Long reportId,

        @Schema(description = "처리 상태", example = "PENDING")
        ReportStatus status,

        @Schema(description = "생성 시각", example = "2026-06-28T22:00:00")
        LocalDateTime createdAt
) {
    public static ReportCreateResponse from(Report report) {
        return new ReportCreateResponse(
                report.getReportId(),
                report.getStatus(),
                report.getCreatedAt()
        );
    }
}
