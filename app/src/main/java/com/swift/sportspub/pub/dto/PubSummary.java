package com.swift.sportspub.pub.dto;

import com.swift.sportspub.pub.entity.Pub;
import com.swift.sportspub.pub.entity.Region;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "펍 1건 요약 (즐겨찾기/검색 카드 응답용)")
public record PubSummary(

        @Schema(description = "펍 ID", example = "12")
        Long pubId,

        @Schema(description = "펍 이름", example = "타이거즈 펍 강남점")
        String name,

        @Schema(description = "주소", example = "서울 강남구 테헤란로 123")
        String address,

        @Schema(description = "지역 코드", example = "GANGNAM")
        Region region,

        @Schema(description = "대표 이미지 URL (없을 시 null)", example = "https://cdn.example.com/pubs/12/1.jpg")
        String thumbnailUrl
) {
    public static PubSummary of(Pub pub, String thumbnailUrl) {
        return new PubSummary(
                pub.getPubId(),
                pub.getName(),
                pub.getAddress(),
                pub.getRegion(),
                thumbnailUrl
        );
    }
}
