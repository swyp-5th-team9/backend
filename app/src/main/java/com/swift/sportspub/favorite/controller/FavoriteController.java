package com.swift.sportspub.favorite.controller;

import com.swift.sportspub.common.response.ApiResponse;
import com.swift.sportspub.common.swagger.DocResponse;
import com.swift.sportspub.common.swagger.DocResponses;
import com.swift.sportspub.favorite.dto.FavoriteListResponse;
import com.swift.sportspub.favorite.service.FavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
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
                    pubName, thumbnailImageUrl은 Pub 도메인 연동 후 제공된다.
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
}
