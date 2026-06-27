package com.swift.sportspub.favorite.controller;

import com.swift.sportspub.common.response.ApiResponse;
import com.swift.sportspub.favorite.service.FavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Favorite", description = "펍 즐겨찾기 API")
@RestController
@RequestMapping("/api/v1/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @Operation(
            summary = "펍 즐겨찾기 추가",
            description = """
                    현재 로그인한 사용자의 펍 즐겨찾기를 등록한다.
                    존재하지 않거나 삭제된 pubId는 400으로 반환한다.
                    이미 즐겨찾기한 pub은 409로 반환한다.
                    사용자당 최대 30개까지 등록할 수 있다.
                    """
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "즐겨찾기 등록 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400", description = "존재하지 않는 pubId 또는 즐겨찾기 30개 초과"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401", description = "인증 필요"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404", description = "사용자 없음"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409", description = "이미 즐겨찾기한 pub"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500", description = "서버 오류"
            )
    })
    @PostMapping("/{pubId}")
    public ApiResponse<Void> addFavorite(
            @AuthenticationPrincipal Long userId,
            @Parameter(description = "즐겨찾기할 펍 ID", example = "1")
            @PathVariable Long pubId
    ) {
        favoriteService.addFavorite(userId, pubId);
        return ApiResponse.success();
    }
}
