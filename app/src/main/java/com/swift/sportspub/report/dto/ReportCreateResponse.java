package com.swift.sportspub.report.dto;

import com.swift.sportspub.report.entity.Report;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "제보 생성 응답")
public record ReportCreateResponse(

        @Schema(description = "생성된 제보 ID", example = "21")
        Long reportId
) {
    public static ReportCreateResponse from(Report report) {
        return new ReportCreateResponse(report.getReportId());
    }
}
