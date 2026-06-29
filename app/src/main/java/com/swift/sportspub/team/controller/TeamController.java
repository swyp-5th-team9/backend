package com.swift.sportspub.team.controller;

import com.swift.sportspub.common.response.ApiResponse;
import com.swift.sportspub.common.swagger.DocResponse;
import com.swift.sportspub.common.swagger.DocResponses;
import com.swift.sportspub.team.dto.TeamListResponse;
import com.swift.sportspub.team.entity.SportType;
import com.swift.sportspub.team.service.TeamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Team", description = "응원 구단 조회 API")
@RestController
@RequestMapping("/api/v1/teams")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;

    @Operation(
            summary = "응원 구단 목록 조회",
            description = """
                    스포츠 종목별 구단 목록. team_id 오름차순으로 반환.
                    앱은 이 응답을 기준으로 team_id ↔ 구단 매핑을 구성한다 (하드코딩 X).
                    sportType 미전송 시 KBO.
                    """
    )
    @DocResponses({
            @DocResponse(responseCode = "200", description = "조회 성공"),
            @DocResponse(responseCode = "401", description = "인증 필요"),
            @DocResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping
    public ApiResponse<TeamListResponse> getList(
            @Parameter(description = "스포츠 종목 (미전송 시 KBO)", example = "KBO")
            @RequestParam(required = false) SportType sportType
    ) {
        return ApiResponse.success(teamService.getList(sportType));
    }
}
