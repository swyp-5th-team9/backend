package com.swift.sportspub.favorite.controller;

import com.swift.sportspub.common.response.ApiResponse;
import com.swift.sportspub.common.swagger.DocResponse;
import com.swift.sportspub.common.swagger.DocResponses;
import com.swift.sportspub.favorite.dto.FavoriteDeleteRequest;
import com.swift.sportspub.favorite.dto.FavoriteListResponse;
import com.swift.sportspub.favorite.service.FavoriteService;
import io.swagger.v3.oas.annotations.Operation;
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
                    """
    )
    @DocResponses({
            @DocResponse(responseCode = "200", description = "조회 성공"),
            @DocResponse(responseCode = "401", description = "인증 필요"),
            @DocResponse(responseCode = "500", description = "서버 오류")
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
            @DocResponse(responseCode = "400", description = "입력값 오류"),
            @DocResponse(responseCode = "401", description = "인증 필요"),
            @DocResponse(responseCode = "404", description = "존재하지 않거나 삭제할 수 없는 즐겨찾기 포함"),
            @DocResponse(responseCode = "500", description = "서버 오류")
    })
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
                    성공 시 생성된 favoriteId를 반환한다.
                    """
    )
    @DocResponses({
            @DocResponse(responseCode = "200", description = "추가 성공"),
            @DocResponse(responseCode = "401", description = "인증 필요"),
            @DocResponse(responseCode = "404", description = "존재하지 않는 펍"),
            @DocResponse(responseCode = "409", description = "이미 즐겨찾기한 펍"),
            @DocResponse(responseCode = "400", description = "즐겨찾기 30개 초과"),
            @DocResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/{pubId}")
    public ApiResponse<Long> addFavorite(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long pubId
    ) {
        return ApiResponse.success(favoriteService.addFavorite(userId, pubId));
    }
}
