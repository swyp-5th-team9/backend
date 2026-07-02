package com.swift.sportspub.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    private static final String BEARER_AUTH = "bearerAuth";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("SportsPub API")
                        .description("""
                                ## 공통 응답
                                - 성공: `{ "success": true, "data": ... }` (data는 null일 수 있음)
                                - 실패: `{ "success": false, "errorCode": "...", "message": "..." }` (#60)
                                  - data 필드는 포함되지 않음
                                
                                ## errorCode 출처
                                - auth / user / favorite: ErrorCode enum name
                                  (INVALID_INPUT, UNAUTHORIZED, FORBIDDEN, NOT_FOUND, CONFLICT, INTERNAL_ERROR)
                                - report: ReportErrorCode enum name
                                  (INVALID_REPORT_CATEGORY, REPORT_CONTENT_REQUIRED, REPORT_CONTENT_TOO_LONG,
                                  REPORT_IMAGE_LIMIT_EXCEEDED, REPORT_IMAGE_SIZE_EXCEEDED, UNSUPPORTED_IMAGE_FORMAT,
                                  PUB_NOT_FOUND, INTERNAL_SERVER_ERROR)
                                - report API에서 UserNotFoundException 등 BusinessException: ErrorCode name (예: NOT_FOUND)
                                
                                ## Android 호환
                                - 기존 `{ success, message }` 파싱은 그대로 동작 (errorCode는 additive 필드)
                                - Report API 실패 body 형식은 변경 없음 (기존에도 errorCode 포함)
                                """))
                .addSecurityItem(new SecurityRequirement().addList(BEARER_AUTH))
                .components(new Components()
                        .addSecuritySchemes(BEARER_AUTH, new SecurityScheme()
                                .name(BEARER_AUTH)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}
