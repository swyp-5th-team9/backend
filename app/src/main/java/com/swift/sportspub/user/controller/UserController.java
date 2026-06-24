package com.swift.sportspub.user.controller;

import com.swift.sportspub.common.response.ApiResponse;
import com.swift.sportspub.user.dto.OnboardingRequest;
import com.swift.sportspub.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
                    teamIds는 최대 3개까지 받을 수 있지만, 현재 Team 도메인이 없어 저장하지 않는다.
                    """
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "온보딩 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 필요"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "사용자 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/me/onboarding")
    public ApiResponse<Void> onboarding(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody OnboardingRequest request
    ) {
        userService.onboarding(userId, request);
        return ApiResponse.success();
    }
}
