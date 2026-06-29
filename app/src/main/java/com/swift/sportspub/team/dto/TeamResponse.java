package com.swift.sportspub.team.dto;

import com.swift.sportspub.team.entity.SportType;
import com.swift.sportspub.team.entity.Team;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "구단 정보")
public record TeamResponse(

        @Schema(description = "구단 ID", example = "1")
        Long teamId,

        @Schema(description = "구단 정식 명칭", example = "LG 트윈스")
        String name,

        @Schema(description = "구단 약칭", example = "LG")
        String shortName,

        @Schema(description = "스포츠 종목", example = "KBO")
        SportType sportType,

        @Schema(description = "홈 구장 (없을 수 있음)", example = "서울 잠실야구장")
        String homeStadium
) {
    public static TeamResponse from(Team team) {
        return new TeamResponse(
                team.getTeamId(),
                team.getName(),
                team.getShortName(),
                team.getSportType(),
                team.getHomeStadium()
        );
    }
}
