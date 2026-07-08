package com.swift.sportspub.user.controller;

import com.swift.sportspub.common.response.ApiResponse;
import com.swift.sportspub.common.swagger.ApiFailResponse;
import com.swift.sportspub.common.swagger.DocResponse;
import com.swift.sportspub.common.swagger.DocResponses;
import com.swift.sportspub.user.dto.OnboardingRequest;
import com.swift.sportspub.user.dto.UpdateUserRequest;
import com.swift.sportspub.user.dto.UserResponse;
import com.swift.sportspub.user.dto.WithdrawRequest;
import com.swift.sportspub.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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
                    nickname은 필수이며 2~20자까지 입력할 수 있다.
                    teamIds를 전달하면 선호 구단으로 저장하며, 최대 3개까지 선택할 수 있다.
                    teamIds를 생략하거나 빈 배열([])을 전달하면 선호 구단 없이 온보딩을 완료한다.
                    존재하지 않는 teamId는 400으로 반환한다.
                    이미 온보딩을 완료한 사용자가 다시 호출하면 409로 반환한다.
                    """
    )
    @DocResponses({
            @DocResponse(responseCode = "200", description = "온보딩 성공"),
            @DocResponse(
                    responseCode = "400",
                    description = "INVALID_INPUT — nickname 검증 실패, teamIds 검증 실패, 존재하지 않는 teamId",
                    content = @Content(
                            schema = @Schema(implementation = ApiFailResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "nickname 필수",
                                            value = "{\"success\":false,\"errorCode\":\"INVALID_INPUT\",\"message\":\"nickname: nickname은 필수입니다.\"}"
                                    ),
                                    @ExampleObject(
                                            name = "존재하지 않는 teamId",
                                            value = "{\"success\":false,\"errorCode\":\"INVALID_INPUT\",\"message\":\"존재하지 않는 teamId가 포함되어 있습니다.\"}"
                                    )
                            }
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
                    description = "NOT_FOUND — 사용자 없음",
                    content = @Content(
                            schema = @Schema(implementation = ApiFailResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"success\":false,\"errorCode\":\"NOT_FOUND\",\"message\":\"사용자를 찾을 수 없습니다.\"}"
                            )
                    )
            ),
            @DocResponse(
                    responseCode = "409",
                    description = "CONFLICT — 이미 온보딩 완료",
                    content = @Content(
                            schema = @Schema(implementation = ApiFailResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"success\":false,\"errorCode\":\"CONFLICT\",\"message\":\"이미 온보딩이 완료된 사용자입니다.\"}"
                            )
                    )
            ),
            @DocResponse(
                    responseCode = "500",
                    description = "INTERNAL_ERROR",
                    content = @Content(schema = @Schema(implementation = ApiFailResponse.class))
            )
    })
    @PostMapping("/me/onboarding")
    public ApiResponse<Void> onboarding(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody OnboardingRequest request
    ) {
        userService.onboarding(userId, request);
        return ApiResponse.success();
    }

    @Operation(
            summary = "내 정보 조회",
            description = """
                    현재 로그인한 사용자의 정보를 조회한다.
                    profileImageUrl은 프로필 이미지 S3 URL이며, 미설정 시 null이다.
                    favoriteTeams에는 저장된 선호 구단의 teamId와 teamName(구단 약칭)이 포함된다.
                    """
    )
    @DocResponses({
            @DocResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))
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
                    description = "NOT_FOUND — 사용자 없음",
                    content = @Content(
                            schema = @Schema(implementation = ApiFailResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"success\":false,\"errorCode\":\"NOT_FOUND\",\"message\":\"사용자를 찾을 수 없습니다.\"}"
                            )
                    )
            ),
            @DocResponse(
                    responseCode = "500",
                    description = "INTERNAL_ERROR",
                    content = @Content(schema = @Schema(implementation = ApiFailResponse.class))
            )
    })
    @GetMapping("/me")
    public ApiResponse<UserResponse> getMyInfo(
            @AuthenticationPrincipal Long userId
    ) {
        return ApiResponse.success(userService.getMyInfo(userId));
    }

    @Operation(
            summary = "내 정보 수정",
            description = """
                    현재 로그인한 사용자의 회원 정보를 수정한다.
                    multipart/form-data로 nickname, teamIds, profileImage를 전달할 수 있다.
                    nickname은 2~20자까지 입력할 수 있고, teamIds는 최대 3개까지 선택할 수 있다.
                    teamIds는 teamIds=1&teamIds=3 형태로 반복 전달한다.
                    profileImage는 선택 값이며 최대 1장, 파일당 10MB 이하, jpeg/png/gif/webp만 허용한다.
                    미전달 필드는 기존 값을 유지한다. profileImage를 빈 파일로 전달해도 기존 이미지를 유지한다.
                    teamIds를 전달하면 기존 선호 구단을 새 목록으로 교체하며, 빈 값 없이 전달 시 전체 해제한다.
                    nickname만 전달하면 선호 구단·프로필 이미지는 변경되지 않는다.
                    teamIds에 중복 ID가 포함되면 중복을 제거한 뒤 저장한다.
                    존재하지 않는 teamId는 400으로 반환한다.
                    """,
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            schema = @Schema(implementation = UpdateUserRequest.class)
                    )
            )
    )
    @DocResponses({
            @DocResponse(responseCode = "200", description = "수정 성공"),
            @DocResponse(
                    responseCode = "400",
                    description = "INVALID_INPUT — nickname/teamIds 검증, 이미지 형식·용량, multipart 오류, 존재하지 않는 teamId",
                    content = @Content(
                            schema = @Schema(implementation = ApiFailResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "프로필 이미지 용량 초과",
                                            value = "{\"success\":false,\"errorCode\":\"INVALID_INPUT\",\"message\":\"프로필 이미지 용량(10MB) 초과\"}"
                                    ),
                                    @ExampleObject(
                                            name = "지원하지 않는 이미지 형식",
                                            value = "{\"success\":false,\"errorCode\":\"INVALID_INPUT\",\"message\":\"지원하지 않는 이미지 형식\"}"
                                    ),
                                    @ExampleObject(
                                            name = "존재하지 않는 teamId",
                                            value = "{\"success\":false,\"errorCode\":\"INVALID_INPUT\",\"message\":\"존재하지 않는 teamId가 포함되어 있습니다.\"}"
                                    )
                            }
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
                    description = "NOT_FOUND — 사용자 없음",
                    content = @Content(
                            schema = @Schema(implementation = ApiFailResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"success\":false,\"errorCode\":\"NOT_FOUND\",\"message\":\"사용자를 찾을 수 없습니다.\"}"
                            )
                    )
            ),
            @DocResponse(
                    responseCode = "500",
                    description = "INTERNAL_ERROR",
                    content = @Content(schema = @Schema(implementation = ApiFailResponse.class))
            )
    })
    @PatchMapping(value = "/me", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<Void> updateMyInfo(
            @AuthenticationPrincipal Long userId,
            @Valid @ModelAttribute UpdateUserRequest request
    ) {
        userService.updateMyInfo(userId, request);
        return ApiResponse.success();
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
    @DocResponses({
            @DocResponse(responseCode = "200", description = "탈퇴 성공"),
            @DocResponse(
                    responseCode = "400",
                    description = "INVALID_INPUT — reasonCode/detail 검증 실패",
                    content = @Content(
                            schema = @Schema(implementation = ApiFailResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"success\":false,\"errorCode\":\"INVALID_INPUT\",\"message\":\"reasonCode: reasonCode는 필수입니다.\"}"
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
                    description = "NOT_FOUND — 사용자 없음",
                    content = @Content(
                            schema = @Schema(implementation = ApiFailResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"success\":false,\"errorCode\":\"NOT_FOUND\",\"message\":\"사용자를 찾을 수 없습니다.\"}"
                            )
                    )
            ),
            @DocResponse(
                    responseCode = "500",
                    description = "INTERNAL_ERROR",
                    content = @Content(schema = @Schema(implementation = ApiFailResponse.class))
            )
    })
    @DeleteMapping("/me")
    public ApiResponse<Void> withdraw(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody WithdrawRequest request
    ) {
        userService.withdraw(userId, request);
        return ApiResponse.success();
    }
}
