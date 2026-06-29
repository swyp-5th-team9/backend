package com.swift.sportspub.pub.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "펍 목록 조회 응답")
public record PubListResponse(

        @Schema(description = "현재 페이지 컨텐츠")
        List<PubListItem> content,

        @Schema(description = "현재 페이지 번호 (0부터)", example = "0")
        int page,

        @Schema(description = "페이지 크기", example = "20")
        int size,

        @Schema(description = "전체 요소 수", example = "137")
        long totalElements,

        @Schema(description = "전체 페이지 수", example = "7")
        int totalPages
) {
    public static PubListResponse of(List<PubListItem> content, int page, int size, long totalElements) {
        int totalPages = size == 0 ? 0 : (int) Math.ceil((double) totalElements / size);
        return new PubListResponse(content, page, size, totalElements, totalPages);
    }
}
