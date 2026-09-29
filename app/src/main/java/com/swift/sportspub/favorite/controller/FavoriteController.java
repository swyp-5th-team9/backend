package com.swift.sportspub.favorite.controller;

import com.swift.sportspub.common.response.ApiResponse;
import com.swift.sportspub.common.swagger.ApiFailResponse;
import com.swift.sportspub.common.swagger.DocResponse;
import com.swift.sportspub.common.swagger.DocResponses;
import com.swift.sportspub.favorite.dto.FavoriteDeleteRequest;
import com.swift.sportspub.favorite.dto.FavoriteListResponse;
import com.swift.sportspub.favorite.service.FavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Favorite", description = "즐겨찾기 API")
@RestController
@RequestMapping("/api/v1/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @Operation(
            summary = "즐겨찾기 목록 조회",
            description = """
                    현재 로그인한 사용자의 즐겨찾기 목록을 조회한다.
                    createdAt 내림차순으로 정렬하며 최대 30개까지 반환한다.
                    즐겨찾기가 없으면 빈 배열을 반환한다.
                    pubName, region, thumbnailImageUrl은 연결된 활성 Pub 정보를 기준으로 반환한다.
                    soft-delete된 Pub은 pubId만 반환하고 상세 필드는 null이다.
                    """
    )
    @DocResponses({
            @DocResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = FavoriteListResponse.class))
            ),
            @DocResponse(
                    responseCode = "401",
                    description = "UNAUTHORIZED — 인증 필요",
                    content = @Content(
                            schema = @Schema(implementation = ApiFailResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"success\":false,\"errorCode\":\"UNAUTHORIZED\",\"message\":\"인증이 필요합니다.\"}"
                            )
                    )
            ),
            @DocResponse(
                    responseCode = "500",
                    description = "INTERNAL_ERROR",
                    content = @Content(schema = @Schema(implementation = ApiFailResponse.class))
            )
    })
    @GetMapping
    public ApiResponse<FavoriteListResponse> getMyFavorites(@AuthenticationPrincipal Long userId) {
        return ApiResponse.success(favoriteService.getMyFavorites(userId));
    }

    @Operation(
            summary = "즐겨찾기 삭제",
            description = """
                    favoriteIds에 해당하는 즐겨찾기를 삭제한다.
                    여러 개 일괄 삭제할 수 있으며, favoriteIds는 1개 이상이어야 한다.
                    요청 ID 중 하나라도 존재하지 않거나 본인 소유가 아니면 전체 실패한다.
                    Pub는 삭제하지 않는다.
                    """
    )
    @DocResponses({
            @DocResponse(responseCode = "200", description = "삭제 성공"),
            @DocResponse(
                    responseCode = "400",
                    description = "INVALID_INPUT — favoriteIds 검증 실패",
                    content = @Content(
                            schema = @Schema(implementation = ApiFailResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"success\":false,\"errorCode\":\"INVALID_INPUT\",\"message\":\"favoriteIds: favoriteIds는 1개 이상이어야 합니다.\"}"
                            )
                    )
            ),
            @DocResponse(
                    responseCode = "401",
                    description = "UNAUTHORIZED — 인증 필요",
                    content = @Content(
                            schema = @Schema(implementation = ApiFailResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"success\":false,\"errorCode\":\"UNAUTHORIZED\",\"message\":\"인증이 필요합니다.\"}"
                            )
                    )
            ),
            @DocResponse(
                    responseCode = "404",
                    description = "NOT_FOUND — 존재하지 않거나 삭제할 수 없는 즐겨찾기 포함",
                    content = @Content(
                            schema = @Schema(implementation = ApiFailResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"success\":false,\"errorCode\":\"NOT_FOUND\",\"message\":\"존재하지 않거나 삭제할 수 없는 즐겨찾기가 포함되어 있습니다.\"}"
                            )
                    )
            ),
            @DocResponse(
                    responseCode = "500",
                    description = "INTERNAL_ERROR",
                    content = @Content(schema = @Schema(implementation = ApiFailResponse.class))
            )
    })
    /*
     * Android 클라이언트 구현 및 RequestBody 일괄 삭제 호환성을 위해 POST /favorites/delete를 유지한다.
     */
    @PostMapping("/delete")
    public ApiResponse<Void> deleteFavorites(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody FavoriteDeleteRequest request
    ) {
        favoriteService.deleteFavorites(userId, request);
        return ApiResponse.success();
    }

    @Operation(
            summary = "즐겨찾기 추가",
            description = """
                    로그인한 사용자가 펍을 즐겨찾기에 추가한다.
                    동일한 펍은 중복 등록할 수 없으며, 사용자당 최대 30개까지 등록할 수 있다.
                    성공 시 data에 생성된 favoriteId(Long)를 반환한다.
                    """
    )
    @DocResponses({
            @DocResponse(
                    responseCode = "200",
                    description = "추가 성공 — data: 생성된 favoriteId (Long)"
            ),
            @DocResponse(
                    responseCode = "400",
                    description = "INVALID_INPUT — 즐겨찾기 30개 초과",
                    content = @Content(
                            schema = @Schema(implementation = ApiFailResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"success\":false,\"errorCode\":\"INVALID_INPUT\",\"message\":\"즐겨찾기는 최대 30개까지 등록할 수 있습니다.\"}"
                            )
                    )
            ),
            @DocResponse(
                    responseCode = "401",
                    description = "UNAUTHORIZED — 인증 필요",
                    content = @Content(
                            schema = @Schema(implementation = ApiFailResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"success\":false,\"errorCode\":\"UNAUTHORIZED\",\"message\":\"인증이 필요합니다.\"}"
                            )
                    )
            ),
            @DocResponse(
                    responseCode = "404",
                    description = "NOT_FOUND — 존재하지 않는 pubId 또는 사용자 없음",
                    content = @Content(
                            schema = @Schema(implementation = ApiFailResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "존재하지 않는 pubId",
                                            value = "{\"success\":false,\"errorCode\":\"NOT_FOUND\",\"message\":\"존재하지 않는 pubId입니다.\"}"
                                    ),
                                    @ExampleObject(
                                            name = "사용자 없음",
                                            value = "{\"success\":false,\"errorCode\":\"NOT_FOUND\",\"message\":\"사용자를 찾을 수 없습니다.\"}"
                                    )
                            }
                    )
            ),
            @DocResponse(
                    responseCode = "409",
                    description = "CONFLICT — 이미 즐겨찾기한 pub",
                    content = @Content(
                            schema = @Schema(implementation = ApiFailResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"success\":false,\"errorCode\":\"CONFLICT\",\"message\":\"이미 즐겨찾기한 pub입니다.\"}"
                            )
                    )
            ),
            @DocResponse(
                    responseCode = "500",
                    description = "INTERNAL_ERROR",
                    content = @Content(schema = @Schema(implementation = ApiFailResponse.class))
            )
    })
    @PostMapping("/{pubId}")
    public ApiResponse<Long> addFavorite(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long pubId
    ) {
        return ApiResponse.success(favoriteService.addFavorite(userId, pubId));
    }
}
