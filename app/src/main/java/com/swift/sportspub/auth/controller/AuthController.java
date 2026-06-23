package com.swift.sportspub.auth.controller;

import com.swift.sportspub.auth.dto.LoginResponse;
import com.swift.sportspub.auth.dto.SocialLoginRequest;
import com.swift.sportspub.auth.service.AuthService;
import com.swift.sportspub.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth", description = "인증 API")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /*
     * Controller는 HTTP 요청/응답 처리에 집중한다.
     *
     * accessToken 요청 값을 검증하고 공통 응답 포맷으로 감싸는 일만 담당하며,
     * 카카오 사용자 정보 조회, 회원 조회/생성, JWT 발급 같은 로그인 비즈니스 로직은 AuthService에 위임한다.
     * 이렇게 계층을 분리하면 API 표현 방식이 바뀌어도 로그인 정책을 재사용할 수 있고 테스트 범위도 명확해진다.
     */
    @Operation(
            summary = "카카오 로그인",
            description = """
                    앱에서 카카오 SDK 로그인을 완료한 뒤 발급받은 Access Token을 전달한다.
                    백엔드는 카카오 사용자 정보를 조회하고 회원 여부를 확인한 후 JWT를 발급한다.
                    최초 로그인 사용자는 자동 회원 생성 후 온보딩 여부를 반환한다.
                    """
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "카카오 로그인 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "유효하지 않은 카카오 토큰"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/login/kakao")
    public ApiResponse<LoginResponse> loginWithKakao(@Valid @RequestBody SocialLoginRequest request) {
        return ApiResponse.success(authService.loginWithKakao(request.accessToken()));
    }

    /*
     * 네이버 로그인도 동일하게 Controller는 엔드포인트와 요청/응답 형식만 담당한다.
     * 플랫폼별 실제 인증 처리는 AuthService와 NaverClient로 분리되어 있어 Controller가 외부 API 세부사항을 알 필요가 없다.
     */
    @Operation(
            summary = "네이버 로그인",
            description = """
                    앱에서 네이버 SDK 로그인을 완료한 뒤 발급받은 Access Token을 전달한다.
                    백엔드는 네이버 사용자 정보를 조회하고 회원 여부를 확인한 후 JWT를 발급한다.
                    최초 로그인 사용자는 자동 회원 생성 후 온보딩 여부를 반환한다.
                    """
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "네이버 로그인 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "유효하지 않은 네이버 토큰"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/login/naver")
    public ApiResponse<LoginResponse> loginWithNaver(@Valid @RequestBody SocialLoginRequest request) {
        return ApiResponse.success(authService.loginWithNaver(request.accessToken()));
    }
}
