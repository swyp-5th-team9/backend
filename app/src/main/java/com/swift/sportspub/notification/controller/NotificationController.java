package com.swift.sportspub.notification.controller;

import com.swift.sportspub.common.response.ApiResponse;
import com.swift.sportspub.common.swagger.ApiFailResponse;
import com.swift.sportspub.common.swagger.DocResponse;
import com.swift.sportspub.common.swagger.DocResponses;
import com.swift.sportspub.notification.dto.NotificationResponse;
import com.swift.sportspub.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Notification", description = "알림 API")
@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(
            summary = "알림 목록 조회",
            description = """
                    현재 로그인한 사용자의 알림 목록을 최신순으로 조회한다.
                    teamIds는 알림 대상 경기의 홈팀·원정팀 ID이며 DB에 저장하지 않는다.
                    알림이 없으면 data는 빈 배열이다.
                    """
    )
    @DocResponses({
            @DocResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(
                            array = @ArraySchema(schema = @Schema(implementation = NotificationResponse.class))
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
                    responseCode = "500",
                    description = "INTERNAL_ERROR",
                    content = @Content(schema = @Schema(implementation = ApiFailResponse.class))
            )
    })
    @GetMapping
    public ApiResponse<List<NotificationResponse>> getMyNotifications(
            @AuthenticationPrincipal Long userId
    ) {
        return ApiResponse.success(notificationService.getMyNotifications(userId));
    }

    @Operation(
            summary = "알림 읽음 처리",
            description = """
                    현재 로그인한 사용자의 특정 알림을 읽음 상태로 변경한다.
                    이미 읽은 알림을 다시 요청해도 성공한다.
                    """
    )
    @DocResponses({
            @DocResponse(responseCode = "200", description = "읽음 처리 성공"),
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
                    description = "NOT_FOUND — 존재하지 않거나 본인 알림이 아님",
                    content = @Content(
                            schema = @Schema(implementation = ApiFailResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"success\":false,\"errorCode\":\"NOT_FOUND\",\"message\":\"존재하지 않거나 처리할 수 없는 알림입니다.\"}"
                            )
                    )
            ),
            @DocResponse(
                    responseCode = "500",
                    description = "INTERNAL_ERROR",
                    content = @Content(schema = @Schema(implementation = ApiFailResponse.class))
            )
    })
    @PatchMapping("/{notificationId}/read")
    public ApiResponse<Void> readNotification(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long notificationId
    ) {
        notificationService.readNotification(userId, notificationId);
        return ApiResponse.success();
    }

    @Operation(
            summary = "알림 삭제",
            description = "현재 로그인한 사용자의 특정 알림을 영구 삭제한다."
    )
    @DocResponses({
            @DocResponse(responseCode = "200", description = "삭제 성공"),
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
                    responseCode = "403",
                    description = "NOTIFICATION_ACCESS_DENIED — 본인 알림이 아님",
                    content = @Content(
                            schema = @Schema(implementation = ApiFailResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"success\":false,\"errorCode\":\"NOTIFICATION_ACCESS_DENIED\",\"message\":\"해당 알림에 대한 권한이 없습니다.\"}"
                            )
                    )
            ),
            @DocResponse(
                    responseCode = "404",
                    description = "NOT_FOUND — 존재하지 않는 알림",
                    content = @Content(
                            schema = @Schema(implementation = ApiFailResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"success\":false,\"errorCode\":\"NOT_FOUND\",\"message\":\"알림을 찾을 수 없습니다.\"}"
                            )
                    )
            ),
            @DocResponse(
                    responseCode = "500",
                    description = "INTERNAL_ERROR",
                    content = @Content(schema = @Schema(implementation = ApiFailResponse.class))
            )
    })
    @DeleteMapping("/{notificationId}")
    public ApiResponse<Void> deleteNotification(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long notificationId
    ) {
        notificationService.deleteNotification(userId, notificationId);
        return ApiResponse.success();
    }
}
