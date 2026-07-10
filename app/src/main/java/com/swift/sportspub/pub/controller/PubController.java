package com.swift.sportspub.pub.controller;

import com.swift.sportspub.common.exception.BusinessException;
import com.swift.sportspub.common.exception.ErrorCode;
import com.swift.sportspub.common.response.ApiResponse;
import com.swift.sportspub.common.swagger.ApiFailResponse;
import com.swift.sportspub.common.swagger.DocResponse;
import com.swift.sportspub.common.swagger.DocResponses;
import com.swift.sportspub.pub.dto.PubDetailResponse;
import com.swift.sportspub.pub.dto.PubFilterParams;
import com.swift.sportspub.pub.dto.PubListResponse;
import com.swift.sportspub.pub.dto.PubListSearchCondition;
import com.swift.sportspub.pub.dto.PubMapResponse;
import com.swift.sportspub.pub.dto.PubMapSearchCondition;
import com.swift.sportspub.pub.service.PubQueryService;
import com.swift.sportspub.pub.service.RegionFilter;
import com.swift.sportspub.pub.service.RegionResolver;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

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
                    지역(region 단일 또는 regions 다중 OR — 자치구/광역/sub 코드) ·
                    시설/스타일/테마/음식 코드(각 AND 매칭) ·
                    수용 규모 · 영업중 여부(openNow) · 영업요일(businessDay — 선택 요일 전부 영업).
                    페이징(0부터, 기본 20·최대 50), 정렬 favorite_count DESC.
                    teamId/teamIds, region/regions 동시 전송 시 다중(복수형)이 우선.
                    """
    )
    @DocResponses({
            @DocResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = PubListResponse.class))
            ),
            @DocResponse(
                    responseCode = "400",
                    description = "INVALID_INPUT — 잘못된 파라미터",
                    content = @Content(schema = @Schema(implementation = ApiFailResponse.class))
            ),
            @DocResponse(
                    responseCode = "401",
                    description = "UNAUTHORIZED — 인증 필요",
                    content = @Content(schema = @Schema(implementation = ApiFailResponse.class))
            ),
            @DocResponse(
                    responseCode = "500",
                    description = "INTERNAL_ERROR",
                    content = @Content(schema = @Schema(implementation = ApiFailResponse.class))
            )
    })
    @GetMapping
    public ApiResponse<PubListResponse> getList(
            @Parameter(description = "이름/주소 ILIKE 검색", example = "치어스")
            @RequestParam(required = false) String keyword,

            @ParameterObject PubFilterParams filter,

            @Parameter(description = "페이지 (0부터)", example = "0")
            @RequestParam(required = false, defaultValue = "0") int page,

            @Parameter(description = "페이지 크기 (max 50)", example = "20")
            @RequestParam(required = false, defaultValue = "20") int size
    ) {
        if (page < 0 || size <= 0 || size > MAX_PAGE_SIZE) {
            throw new BusinessException(ErrorCode.INVALID_INPUT);
        }
        RegionFilter regionFilter = RegionResolver.resolve(filter.mergedRegions());
        PubListSearchCondition condition = new PubListSearchCondition(
                keyword, filter.mergedTeamIds(), regionFilter.regions(), regionFilter.subRegion(),
                filter.facilityCodes(), filter.styleCodes(), filter.themeCodes(), filter.foodCodes(),
                filter.capacityRange(), filter.openNow(), filter.businessDay(),
                page, size == 0 ? DEFAULT_PAGE_SIZE : size
        );
        return ApiResponse.success(pubQueryService.findList(condition));
    }

    @Operation(
            summary = "지도 펍 조회 (BBox)",
            description = """
                    지도 화면에서 보이는 BBox 안의 펍 마커를 조회한다.
                    페이징 없이 좌표·이름·상태·찜 수·태그 필드(상영 구단·시설·스타일·테마·음식·썸네일)를 포함한 응답.
                    필터: 응원 구단(teamId 단일 또는 teamIds 다중 OR) ·
                    지역(region 단일 또는 regions 다중 OR — 자치구/광역/sub 코드) ·
                    시설/스타일/테마/음식 코드(각 AND 매칭) · 수용 규모 · 영업중 여부(openNow) ·
                    영업요일(businessDay — 선택 요일 전부 영업).
                    teamId/teamIds, region/regions 동시 전송 시 다중(복수형)이 우선.
                    """
    )
    @DocResponses({
            @DocResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = PubMapResponse.class))
            ),
            @DocResponse(
                    responseCode = "400",
                    description = "INVALID_INPUT — BBox 좌표 누락 또는 범위 오류",
                    content = @Content(schema = @Schema(implementation = ApiFailResponse.class))
            ),
            @DocResponse(
                    responseCode = "401",
                    description = "UNAUTHORIZED — 인증 필요",
                    content = @Content(schema = @Schema(implementation = ApiFailResponse.class))
            ),
            @DocResponse(
                    responseCode = "500",
                    description = "INTERNAL_ERROR",
                    content = @Content(schema = @Schema(implementation = ApiFailResponse.class))
            )
    })
    @GetMapping("/map")
    public ApiResponse<PubMapResponse> getMapMarkers(
            @Parameter(description = "남서쪽 위도", example = "37.49") @RequestParam BigDecimal swLat,
            @Parameter(description = "남서쪽 경도", example = "127.02") @RequestParam BigDecimal swLng,
            @Parameter(description = "북동쪽 위도", example = "37.51") @RequestParam BigDecimal neLat,
            @Parameter(description = "북동쪽 경도", example = "127.04") @RequestParam BigDecimal neLng,

            @ParameterObject PubFilterParams filter
    ) {
        RegionFilter regionFilter = RegionResolver.resolve(filter.mergedRegions());
        PubMapSearchCondition condition = new PubMapSearchCondition(
                swLat, swLng, neLat, neLng,
                filter.mergedTeamIds(), regionFilter.regions(), regionFilter.subRegion(),
                filter.facilityCodes(), filter.styleCodes(), filter.themeCodes(), filter.foodCodes(),
                filter.capacityRange(), filter.openNow(), filter.businessDay()
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
            @DocResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = PubDetailResponse.class))
            ),
            @DocResponse(
                    responseCode = "401",
                    description = "UNAUTHORIZED — 인증 필요",
                    content = @Content(schema = @Schema(implementation = ApiFailResponse.class))
            ),
            @DocResponse(
                    responseCode = "404",
                    description = "NOT_FOUND — 존재하지 않는 펍",
                    content = @Content(schema = @Schema(implementation = ApiFailResponse.class))
            ),
            @DocResponse(
                    responseCode = "500",
                    description = "INTERNAL_ERROR",
                    content = @Content(schema = @Schema(implementation = ApiFailResponse.class))
            )
    })
    @GetMapping("/{pubId}")
    public ApiResponse<PubDetailResponse> getDetail(
            @Parameter(description = "펍 ID", example = "1") @PathVariable Long pubId
    ) {
        return ApiResponse.success(pubQueryService.findDetail(pubId));
    }
}
