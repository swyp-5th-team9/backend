package com.swift.sportspub.common.swagger;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Swagger {@link ApiResponse} 별칭.
 * {@link com.swift.sportspub.common.response.ApiResponse}와 이름 충돌을 피하기 위해 사용한다.
 */
@Target({ElementType.METHOD, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(DocResponses.class)
@ApiResponse(responseCode = "", description = "")
public @interface DocResponse {

    @AliasFor(annotation = ApiResponse.class, attribute = "responseCode")
    String responseCode();

    @AliasFor(annotation = ApiResponse.class, attribute = "description")
    String description();
}
