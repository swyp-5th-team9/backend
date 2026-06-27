package com.swift.sportspub.auth.controller;

import com.swift.sportspub.auth.BearerTokenExtractor;
import com.swift.sportspub.auth.dto.LoginResponse;
import com.swift.sportspub.auth.dto.TokenReissueRequest;
import com.swift.sportspub.auth.dto.TokenResponse;
import com.swift.sportspub.auth.service.AuthService;
import com.swift.sportspub.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
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
     * Authorization 헤더의 SDK accessToken을 검증하고 공통 응답 포맷으로 감싸는 일만 담당하며,
     * 카카오 사용자 정보 조회, 회원 조회/생성, JWT 발급 같은 로그인 비즈니스 로직은 AuthService에 위임한다.
     */
    @Operation(
            summary = "카카오 로그인",
            description = """
                    앱에서 카카오 SDK 로그인을 완료한 뒤 발급받은 Access Token을 Authorization 헤더로 전달한다.
                    형식: `Authorization: Bearer {카카오_access_token}`
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
    public ApiResponse<LoginResponse> loginWithKakao(
            @Parameter(
                    name = "Authorization",
                    description = "카카오 SDK Access Token (Bearer {token})",
                    required = true,
                    in = ParameterIn.HEADER,
                    example = "Bearer kakao_access_token"
            )
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization
    ) {
        return ApiResponse.success(authService.loginWithKakao(BearerTokenExtractor.extractAccessToken(authorization)));
    }

    /*
     * 네이버 로그인도 동일하게 Controller는 엔드포인트와 요청/응답 형식만 담당한다.
     * 플랫폼별 실제 인증 처리는 AuthService와 NaverClient로 분리되어 있어 Controller가 외부 API 세부사항을 알 필요가 없다.
     */
    @Operation(
            summary = "네이버 로그인",
            description = """
                    앱에서 네이버 SDK 로그인을 완료한 뒤 발급받은 Access Token을 Authorization 헤더로 전달한다.
                    형식: `Authorization: Bearer {네이버_access_token}`
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
    public ApiResponse<LoginResponse> loginWithNaver(
            @Parameter(
                    name = "Authorization",
                    description = "네이버 SDK Access Token (Bearer {token})",
                    required = true,
                    in = ParameterIn.HEADER,
                    example = "Bearer naver_access_token"
            )
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization
    ) {
        return ApiResponse.success(authService.loginWithNaver(BearerTokenExtractor.extractAccessToken(authorization)));
    }

    /*
     * RefreshToken 재발급 요청은 AccessToken이 만료된 상황에서도 호출될 수 있다.
     * Controller는 refreshToken 요청 값을 검증하고, 실제 토큰 검증/회전/저장은 AuthService에 위임한다.
     */
    @Operation(
            summary = "토큰 재발급",
            description = """
                    저장된 RefreshToken을 검증한 뒤 새 AccessToken과 RefreshToken을 발급한다.
                    재발급 성공 시 기존 RefreshToken은 폐기되고 새 RefreshToken으로 교체된다.
                    """
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "토큰 재발급 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "유효하지 않은 refreshToken"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/refresh-token")
    public ApiResponse<TokenResponse> reissue(@Valid @RequestBody TokenReissueRequest request) {
        return ApiResponse.success(authService.reissue(request.refreshToken()));
    }

    /*
     * JwtAuthenticationFilter는 인증 성공 시 SecurityContext principal에 userId(Long)를 저장한다.
     * 따라서 현재 MVP 구조에서는 별도 UserDetails 없이 @AuthenticationPrincipal Long userId로 현재 사용자를 식별한다.
     */
    @Operation(
            summary = "로그아웃",
            description = """
                    현재 인증된 사용자의 RefreshToken을 삭제한다.
                    이후 기존 RefreshToken으로는 토큰 재발급을 받을 수 없다.
                    """
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "로그아웃 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 필요"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/logout")
    public ApiResponse<Void> logout(@AuthenticationPrincipal Long userId) {
        authService.logout(userId);
        return ApiResponse.success();
    }
}
