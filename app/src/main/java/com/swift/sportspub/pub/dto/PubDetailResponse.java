package com.swift.sportspub.pub.dto;

import com.swift.sportspub.pub.entity.CapacityRange;
import com.swift.sportspub.pub.entity.PubStatus;
import com.swift.sportspub.pub.entity.Region;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.List;

@Schema(description = "펍 상세 조회 응답")
public record PubDetailResponse(

        @Schema(description = "펍 ID", example = "1")
        Long pubId,

        @Schema(description = "펍 이름")
        String name,

        @Schema(description = "주소")
        String address,

        @Schema(description = "지역 코드")
        Region region,

        @Schema(description = "위도")
        BigDecimal latitude,

        @Schema(description = "경도")
        BigDecimal longitude,

        @Schema(description = "전화번호")
        String phone,

        @Schema(description = "영업 상태")
        PubStatus status,

        @Schema(description = "수용 규모")
        CapacityRange capacityRange,

        @Schema(description = "단체석 최대 인원")
        Integer groupSeatMaxPeople,

        @Schema(description = "즐겨찾기 수")
        Integer favoriteCount,

        @Schema(description = "설명")
        String description,

        @Schema(description = "이미지 목록 (displayOrder ASC)")
        List<PubImageItem> images,

        @Schema(description = "상영 구단 목록")
        List<SupportedTeamDetail> supportedTeams,

        @Schema(description = "시설 코드 목록")
        List<String> facilityCodes,

        @Schema(description = "스타일 코드 목록")
        List<String> styleCodes,

        @Schema(description = "테마 코드 목록")
        List<String> themeCodes,

        @Schema(description = "음식 코드 목록")
        List<String> foodCodes,

        @Schema(description = "요일별 영업시간 (1~7)")
        List<BusinessHourItem> businessHours,

        @Schema(description = "메뉴 목록 (displayOrder ASC)")
        List<MenuItem> menus
) {
}
