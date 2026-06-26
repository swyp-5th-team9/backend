package com.swift.sportspub.user.controller;

import com.swift.sportspub.user.dto.OnboardingRequest;
import com.swift.sportspub.user.dto.UpdateUserRequest;
import com.swift.sportspub.user.dto.UserResponse;
import com.swift.sportspub.user.dto.WithdrawRequest;
import com.swift.sportspub.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User", description = "회원 API")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(
            summary = "회원 온보딩",
            description = """
                    인증된 회원의 온보딩을 완료한다.
                    nickname은 필수이며 최대 20자까지 입력할 수 있다.
                    현재 온보딩에서는 teamIds를 저장하지 않는다.
                    선호 구단 저장 및 수정은 PATCH /api/v1/users/me 에서 지원한다.
                    Team 도메인 구현 후 teamIds 존재 여부 검증이 추가될 예정이다.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "온보딩 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 필요"),
            @ApiResponse(responseCode = "404", description = "사용자 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/me/onboarding")
    public com.swift.sportspub.common.response.ApiResponse<Void> onboarding(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody OnboardingRequest request
    ) {
        userService.onboarding(userId, request);
        return com.swift.sportspub.common.response.ApiResponse.success();
    }

    @Operation(
            summary = "내 정보 조회",
            description = """
                    현재 로그인한 사용자의 정보를 조회한다.
                    현재는 UserFavoriteTeam 조회 로직과 Team 도메인이 구현되지 않아 favoriteTeams를 빈 배열로 반환한다.
                    Team 도메인 구현 후 favoriteTeams에 teamId, teamName 등 구단 정보를 제공할 예정이다.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "회원 정보 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/me")
    public com.swift.sportspub.common.response.ApiResponse<UserResponse> getMyInfo(
            @AuthenticationPrincipal Long userId
    ) {
        return com.swift.sportspub.common.response.ApiResponse.success(userService.getMyInfo(userId));
    }

    @Operation(
            summary = "내 정보 수정",
            description = """
                    현재 로그인한 사용자의 회원 정보를 수정한다.
                    닉네임 및 선호 구단 정보를 변경할 수 있다.
                    nickname은 최대 20자까지 입력할 수 있고, teamIds는 최대 3개까지 선택할 수 있다.
                    현재는 teamId 값 저장만 수행하며, Team 존재 여부 검증은 Team 도메인 구현 후 추가될 예정이다.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 오류"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "회원 정보 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PatchMapping("/me")
    public com.swift.sportspub.common.response.ApiResponse<Void> updateMyInfo(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody UpdateUserRequest request
    ) {
        userService.updateMyInfo(userId, request);
        return com.swift.sportspub.common.response.ApiResponse.success();
    }

    @Operation(
            summary = "회원 탈퇴",
            description = """
                    현재 로그인한 회원을 탈퇴 처리한다.
                    회원 데이터는 즉시 삭제하지 않고 deletedAt을 저장하는 Soft Delete 방식으로 처리한다.
                    탈퇴 사유(reasonCode)는 필수이며, OTHER를 선택한 경우 detail을 함께 입력해야 한다.
                    OTHER가 아닌 사유를 선택한 경우 detail은 입력할 수 없다.
                    탈퇴 성공 시 서버에 저장된 RefreshToken은 즉시 삭제된다.
                    MVP에서는 동일 OAuth 계정으로 재로그인하면 기존 계정이 복구된다.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "탈퇴 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 오류"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "회원 정보 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @DeleteMapping("/me")
    public com.swift.sportspub.common.response.ApiResponse<Void> withdraw(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody WithdrawRequest request
    ) {
        userService.withdraw(userId, request);
        return com.swift.sportspub.common.response.ApiResponse.success();
    }
}
