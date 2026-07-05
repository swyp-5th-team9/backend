package com.swift.sportspub.pub.controller;

import com.swift.sportspub.common.exception.BusinessException;
import com.swift.sportspub.common.exception.ErrorCode;
import com.swift.sportspub.common.response.ApiResponse;
import com.swift.sportspub.common.swagger.DocResponse;
import com.swift.sportspub.common.swagger.DocResponses;
import com.swift.sportspub.pub.dto.BusinessDayFilter;
import com.swift.sportspub.pub.dto.PubDetailResponse;
import com.swift.sportspub.pub.dto.PubListResponse;
import com.swift.sportspub.pub.dto.PubListSearchCondition;
import com.swift.sportspub.pub.dto.PubMapResponse;
import com.swift.sportspub.pub.dto.PubMapSearchCondition;
import com.swift.sportspub.pub.entity.CapacityRange;
import com.swift.sportspub.pub.service.PubQueryService;
import com.swift.sportspub.pub.service.RegionFilter;
import com.swift.sportspub.pub.service.RegionResolver;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@Tag(name = "Pub", description = "펍 조회 API")
@RestController
@RequestMapping("/api/v1/pubs")
@RequiredArgsConstructor
public class PubController {

    private static final int MAX_PAGE_SIZE = 50;
    private static final int DEFAULT_PAGE_SIZE = 20;

    private final PubQueryService pubQueryService;

    @Operation(
            summary = "펍 목록 조회",
            description = """
                    리스트 화면용 펍 목록.
                    필터: 키워드(이름/주소 ILIKE) · 응원 구단(teamId 단일 또는 teamIds 다중 OR) ·
                    지역(region — 자치구 코드 또는 광역 코드) · 시설/스타일/테마/음식 코드(각 AND 매칭) ·
                    수용 규모 · 영업중 여부(openNow) · 영업요일(businessDay — 선택 요일 전부 영업).
                    페이징(0부터, 기본 20·최대 50), 정렬 favorite_count DESC.
                    teamId 와 teamIds 동시 전송 시 teamIds 우선.
                    """
    )
    @DocResponses({
            @DocResponse(responseCode = "200", description = "조회 성공"),
            @DocResponse(responseCode = "400", description = "잘못된 파라미터"),
            @DocResponse(responseCode = "401", description = "인증 필요"),
            @DocResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping
    public ApiResponse<PubListResponse> getList(
            @Parameter(description = "이름/주소 ILIKE 검색", example = "치어스")
            @RequestParam(required = false) String keyword,

            @Parameter(description = "상영 구단 ID (단일, 호환용)", example = "1")
            @RequestParam(required = false) Long teamId,

            @Parameter(description = "상영 구단 ID 다중 (OR 매칭, 입력 중 하나라도 응원)")
            @RequestParam(required = false) List<Long> teamIds,

            @Parameter(description = "지역 — 자치구 코드/광역 코드/sub 코드(JAMSIL, HONGDAE_HAPJEONG, SANGAM_MANGWON)", example = "GANGNAM")
            @RequestParam(required = false) String region,

            @Parameter(description = "시설 코드 (AND, 예: GROUP_SEAT, PARKING)")
            @RequestParam(required = false) List<String> facilityCodes,

            @Parameter(description = "스타일 코드 (AND, 예: BIG_SCREEN)")
            @RequestParam(required = false) List<String> styleCodes,

            @Parameter(description = "테마 코드 (AND, 예: SPACIOUS_VIEW)")
            @RequestParam(required = false) List<String> themeCodes,

            @Parameter(description = "음식 코드 (AND, 예: CHICKEN, BEER)")
            @RequestParam(required = false) List<String> foodCodes,

            @Parameter(description = "수용 규모", example = "R_50_100")
            @RequestParam(required = false) CapacityRange capacityRange,

            @Parameter(description = "지금 영업중인 펍만 (true)")
            @RequestParam(required = false) Boolean openNow,

            @Parameter(description = "영업요일 — EVERYDAY/WEEKDAY/WEEKEND/MON..SUN (선택 요일 전부 영업)")
            @RequestParam(required = false) BusinessDayFilter businessDay,

            @Parameter(description = "페이지 (0부터)", example = "0")
            @RequestParam(required = false, defaultValue = "0") int page,

            @Parameter(description = "페이지 크기 (max 50)", example = "20")
            @RequestParam(required = false, defaultValue = "20") int size
    ) {
        if (page < 0 || size <= 0 || size > MAX_PAGE_SIZE) {
            throw new BusinessException(ErrorCode.INVALID_INPUT);
        }
        RegionFilter regionFilter = RegionResolver.resolve(region);
        List<Long> mergedTeamIds = mergeTeamIds(teamId, teamIds);
        PubListSearchCondition condition = new PubListSearchCondition(
                keyword, mergedTeamIds, regionFilter.regions(), regionFilter.subRegion(),
                facilityCodes, styleCodes, themeCodes, foodCodes,
                capacityRange, openNow, businessDay,
                page, size == 0 ? DEFAULT_PAGE_SIZE : size
        );
        return ApiResponse.success(pubQueryService.findList(condition));
    }

    private List<Long> mergeTeamIds(Long teamId, List<Long> teamIds) {
        if (teamIds != null && !teamIds.isEmpty()) {
            return teamIds;
        }
        if (teamId != null) {
            return List.of(teamId);
        }
        return List.of();
    }

    @Operation(
            summary = "지도 펍 조회 (BBox)",
            description = """
                    지도 화면에서 보이는 BBox 안의 펍 마커를 조회한다.
                    페이징 없이 좌표·이름·상태·찜 수·태그 필드(상영 구단·시설·스타일·테마·음식·썸네일)를 포함한 응답.
                    필터: 응원 구단(teamId 단일 또는 teamIds 다중 OR) · 지역(region — 자치구/광역/sub 코드) ·
                    시설/스타일/테마/음식 코드(각 AND 매칭) · 수용 규모 · 영업중 여부(openNow) ·
                    영업요일(businessDay — 선택 요일 전부 영업).
                    teamId 와 teamIds 동시 전송 시 teamIds 우선.
                    """
    )
    @DocResponses({
            @DocResponse(responseCode = "200", description = "조회 성공"),
            @DocResponse(responseCode = "400", description = "BBox 좌표 누락 또는 범위 오류"),
            @DocResponse(responseCode = "401", description = "인증 필요"),
            @DocResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/map")
    public ApiResponse<PubMapResponse> getMapMarkers(
            @Parameter(description = "남서쪽 위도", example = "37.49") @RequestParam BigDecimal swLat,
            @Parameter(description = "남서쪽 경도", example = "127.02") @RequestParam BigDecimal swLng,
            @Parameter(description = "북동쪽 위도", example = "37.51") @RequestParam BigDecimal neLat,
            @Parameter(description = "북동쪽 경도", example = "127.04") @RequestParam BigDecimal neLng,

            @Parameter(description = "상영 구단 ID (단일, 호환용)", example = "1")
            @RequestParam(required = false) Long teamId,

            @Parameter(description = "상영 구단 ID 다중 (OR 매칭, 입력 중 하나라도 응원)")
            @RequestParam(required = false) List<Long> teamIds,

            @Parameter(description = "지역 — 자치구 코드/광역 코드/sub 코드(JAMSIL, HONGDAE_HAPJEONG, SANGAM_MANGWON)", example = "GANGNAM")
            @RequestParam(required = false) String region,

            @Parameter(description = "시설 코드 (AND, 예: GROUP_SEAT, PARKING)")
            @RequestParam(required = false) List<String> facilityCodes,

            @Parameter(description = "스타일 코드 (AND, 예: BIG_SCREEN)")
            @RequestParam(required = false) List<String> styleCodes,

            @Parameter(description = "테마 코드 (AND, 예: SPACIOUS_VIEW)")
            @RequestParam(required = false) List<String> themeCodes,

            @Parameter(description = "음식 코드 (AND, 예: CHICKEN, BEER)")
            @RequestParam(required = false) List<String> foodCodes,

            @Parameter(description = "수용 규모", example = "R_50_100")
            @RequestParam(required = false) CapacityRange capacityRange,

            @Parameter(description = "지금 영업중인 펍만 (true)")
            @RequestParam(required = false) Boolean openNow,

            @Parameter(description = "영업요일 — EVERYDAY/WEEKDAY/WEEKEND/MON..SUN (선택 요일 전부 영업)")
            @RequestParam(required = false) BusinessDayFilter businessDay
    ) {
        RegionFilter regionFilter = RegionResolver.resolve(region);
        List<Long> mergedTeamIds = mergeTeamIds(teamId, teamIds);
        PubMapSearchCondition condition = new PubMapSearchCondition(
                swLat, swLng, neLat, neLng,
                mergedTeamIds, regionFilter.regions(), regionFilter.subRegion(),
                facilityCodes, styleCodes, themeCodes, foodCodes,
                capacityRange, openNow, businessDay
        );
        return ApiResponse.success(pubQueryService.findMapMarkers(condition));
    }

    @Operation(
            summary = "펍 상세 조회",
            description = """
                    펍 상세 화면용. 기본 정보 + 이미지·메뉴·시설·요일별 영업시간·상영 구단까지
                    한 번에 반환한다.
                    """
    )
    @DocResponses({
            @DocResponse(responseCode = "200", description = "조회 성공"),
            @DocResponse(responseCode = "401", description = "인증 필요"),
            @DocResponse(responseCode = "404", description = "존재하지 않는 펍"),
            @DocResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/{pubId}")
    public ApiResponse<PubDetailResponse> getDetail(
            @Parameter(description = "펍 ID", example = "1") @PathVariable Long pubId
    ) {
        return ApiResponse.success(pubQueryService.findDetail(pubId));
    }
}
