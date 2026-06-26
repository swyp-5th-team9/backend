package com.swift.sportspub.match.controller;

import com.swift.sportspub.common.response.ApiResponse;
import com.swift.sportspub.match.dto.MatchScheduleResponse;
import com.swift.sportspub.match.service.MatchScheduleService;
import com.swift.sportspub.team.entity.SportType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@Tag(name = "Match", description = "경기 일정 조회 API")
@RestController
@RequestMapping("/api/v1/matches")
@RequiredArgsConstructor
public class MatchScheduleController {

    private final MatchScheduleService matchScheduleService;

    @Operation(
            summary = "경기 일정 조회",
            description = "특정 일자(date) 또는 기간(from~to) + 구단(teamId) 필터로 경기 일정을 조회한다. "
                    + "date, from, to 모두 미전송 시 오늘 일정을 반환한다."
    )
    @GetMapping
    public ApiResponse<MatchScheduleResponse> getSchedule(
            @Parameter(description = "스포츠 종목 (미전송 시 KBO)", example = "KBO")
            @RequestParam(required = false) SportType sportType,

            @Parameter(description = "단일 일자 (YYYY-MM-DD). from/to 와 동시 사용 불가", example = "2026-06-27")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,

            @Parameter(description = "기간 시작 (YYYY-MM-DD). to 와 세트", example = "2026-06-01")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,

            @Parameter(description = "기간 끝 (YYYY-MM-DD). from 와 세트", example = "2026-06-30")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,

            @Parameter(description = "구단 ID — 홈/원정 어느 쪽이든 매칭", example = "1")
            @RequestParam(required = false) Long teamId
    ) {
        return ApiResponse.success(
                matchScheduleService.getSchedule(sportType, date, from, to, teamId)
        );
    }
}
