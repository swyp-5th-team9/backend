package com.swift.sportspub.auth.controller;

import com.swift.sportspub.auth.dto.LoginResponse;
import com.swift.sportspub.auth.dto.TokenReissueRequest;
import com.swift.sportspub.auth.dto.TokenResponse;
import com.swift.sportspub.auth.service.AuthService;
import com.swift.sportspub.common.response.ApiResponse;
import com.swift.sportspub.common.swagger.ApiFailResponse;
import com.swift.sportspub.common.swagger.DocResponse;
import com.swift.sportspub.common.swagger.DocResponses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
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

    @Operation(
            summary = "카카오 로그인",
            security = {},
            description = """
                    앱에서 카카오 SDK 로그인을 완료한 뒤 발급받은 Access Token을 Authorization 헤더로 전달한다.
                    SDK 토큰 문자열만 전달하거나 Bearer 접두사를 붙여 전달해도 된다.
                    백엔드는 카카오 사용자 정보를 조회하고 회원 여부를 확인한 후 JWT를 발급한다.
                    최초 로그인 사용자는 자동 회원 생성 후 온보딩 여부를 반환한다.
                    Authorization 헤더 누락 시 500(INTERNAL_ERROR)이 반환된다.
                    탈퇴 후 30일 이내 동일 OAuth 재로그인 시 계정이 복구되며 restored=true가 반환된다.
                    30일 초과 시 기존 계정은 Hard Delete 후 신규 회원으로 처리된다(restored=false).
                    """
    )
    @DocResponses({
            @DocResponse(responseCode = "200", description = "카카오 로그인 성공"),
            @DocResponse(
                    responseCode = "401",
                    description = "UNAUTHORIZED — 유효하지 않은 카카오 accessToken",
                    content = @Content(
                            schema = @Schema(implementation = ApiFailResponse.class),
                            examples = @ExampleObject(
                                    name = "UNAUTHORIZED",
                                    value = "{\"success\":false,\"errorCode\":\"UNAUTHORIZED\",\"message\":\"유효하지 않은 카카오 accessToken입니다.\"}"
                            )
                    )
            ),
            @DocResponse(
                    responseCode = "500",
                    description = "INTERNAL_ERROR",
                    content = @Content(schema = @Schema(implementation = ApiFailResponse.class))
            )
    })
    @PostMapping("/login/kakao")
    public ApiResponse<LoginResponse> loginWithKakao(
            @Parameter(
                    name = "Authorization",
                    description = "카카오 SDK Access Token",
                    required = true,
                    in = ParameterIn.HEADER,
                    example = "kakao_access_token"
            )
            @RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken
    ) {
        return ApiResponse.success(authService.loginWithKakao(accessToken));
    }

    @Operation(
            summary = "네이버 로그인",
            security = {},
            description = """
                    앱에서 네이버 SDK 로그인을 완료한 뒤 발급받은 Access Token을 Authorization 헤더로 전달한다.
                    SDK 토큰 문자열만 전달하거나 Bearer 접두사를 붙여 전달해도 된다.
                    백엔드는 네이버 사용자 정보를 조회하고 회원 여부를 확인한 후 JWT를 발급한다.
                    최초 로그인 사용자는 자동 회원 생성 후 온보딩 여부를 반환한다.
                    탈퇴 후 30일 이내 동일 OAuth 재로그인 시 계정이 복구되며 restored=true가 반환된다.
                    30일 초과 시 기존 계정은 Hard Delete 후 신규 회원으로 처리된다(restored=false).
                    Authorization 헤더 누락 시 500(INTERNAL_ERROR)이 반환된다.
                    """
    )
    @DocResponses({
            @DocResponse(responseCode = "200", description = "네이버 로그인 성공"),
            @DocResponse(
                    responseCode = "401",
                    description = "UNAUTHORIZED — 유효하지 않은 네이버 accessToken",
                    content = @Content(
                            schema = @Schema(implementation = ApiFailResponse.class),
                            examples = @ExampleObject(
                                    name = "UNAUTHORIZED",
                                    value = "{\"success\":false,\"errorCode\":\"UNAUTHORIZED\",\"message\":\"유효하지 않은 네이버 accessToken입니다.\"}"
                            )
                    )
            ),
            @DocResponse(
                    responseCode = "500",
                    description = "INTERNAL_ERROR",
                    content = @Content(schema = @Schema(implementation = ApiFailResponse.class))
            )
    })
    @PostMapping("/login/naver")
    public ApiResponse<LoginResponse> loginWithNaver(
            @Parameter(
                    name = "Authorization",
                    description = "네이버 SDK Access Token",
                    required = true,
                    in = ParameterIn.HEADER,
                    example = "naver_access_token"
            )
            @RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken
    ) {
        return ApiResponse.success(authService.loginWithNaver(accessToken));
    }

    @Operation(
            summary = "토큰 재발급",
            security = {},
            description = """
                    저장된 RefreshToken을 검증한 뒤 새 AccessToken과 RefreshToken을 발급한다.
                    재발급 성공 시 기존 RefreshToken은 폐기되고 새 RefreshToken으로 교체된다.
                    """
    )
    @DocResponses({
            @DocResponse(responseCode = "200", description = "토큰 재발급 성공"),
            @DocResponse(
                    responseCode = "400",
                    description = "INVALID_INPUT — refreshToken 검증 실패",
                    content = @Content(
                            schema = @Schema(implementation = ApiFailResponse.class),
                            examples = @ExampleObject(
                                    name = "INVALID_INPUT",
                                    value = "{\"success\":false,\"errorCode\":\"INVALID_INPUT\",\"message\":\"refreshToken: refreshToken은 필수입니다.\"}"
                            )
                    )
            ),
            @DocResponse(
                    responseCode = "401",
                    description = "UNAUTHORIZED — 유효하지 않은 refreshToken",
                    content = @Content(
                            schema = @Schema(implementation = ApiFailResponse.class),
                            examples = @ExampleObject(
                                    name = "UNAUTHORIZED",
                                    value = "{\"success\":false,\"errorCode\":\"UNAUTHORIZED\",\"message\":\"유효하지 않은 refreshToken입니다.\"}"
                            )
                    )
            ),
            @DocResponse(
                    responseCode = "500",
                    description = "INTERNAL_ERROR",
                    content = @Content(schema = @Schema(implementation = ApiFailResponse.class))
            )
    })
    @PostMapping("/refresh-token")
    public ApiResponse<TokenResponse> reissue(@Valid @RequestBody TokenReissueRequest request) {
        return ApiResponse.success(authService.reissue(request.refreshToken()));
    }

    @Operation(
            summary = "로그아웃",
            description = """
                    현재 인증된 사용자의 RefreshToken을 삭제한다.
                    이후 기존 RefreshToken으로는 토큰 재발급을 받을 수 없다.
                    """
    )
    @DocResponses({
            @DocResponse(responseCode = "200", description = "로그아웃 성공"),
            @DocResponse(
                    responseCode = "401",
                    description = "UNAUTHORIZED — 인증 필요",
                    content = @Content(
                            schema = @Schema(implementation = ApiFailResponse.class),
                            examples = @ExampleObject(
                                    name = "UNAUTHORIZED",
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
    @PostMapping("/logout")
    public ApiResponse<Void> logout(@AuthenticationPrincipal Long userId) {
        authService.logout(userId);
        return ApiResponse.success();
    }
}
