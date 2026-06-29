package com.swift.sportspub.pub.dto;

import com.swift.sportspub.pub.entity.PubBusinessHours;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalTime;

@Schema(description = "요일별 영업시간 1건")
public record BusinessHourItem(

        @Schema(description = "요일 (ISO 1=월~7=일)", example = "1")
        Short dayOfWeek,

        @Schema(description = "영업 시작", example = "17:00")
        LocalTime openTime,

        @Schema(description = "영업 종료 — open < close 이면 익일 마감", example = "02:00")
        LocalTime closeTime,

        @Schema(description = "휴무 여부", example = "false")
        Boolean isClosed
) {
    public static BusinessHourItem from(PubBusinessHours h) {
        return new BusinessHourItem(
                h.getDayOfWeek(),
                h.getOpenTime(),
                h.getCloseTime(),
                h.getIsClosed()
        );
    }
}
