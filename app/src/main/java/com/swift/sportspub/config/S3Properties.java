package com.swift.sportspub.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * S3 연결 설정 ({@code app.s3.*}).
 *
 * <p>로컬 개발 시 값이 비어 있어도 앱 기동에 영향 없음 ({@link S3Config} 비활성화 상태).
 *
 * <p>[S3 배포 시] {@code .env} 또는 배포 환경에 아래 값을 설정한다.
 * <ul>
 *   <li>{@code S3_BUCKET} → bucket</li>
 *   <li>{@code AWS_REGION} → region (기본 ap-northeast-2)</li>
 *   <li>{@code AWS_ACCESS_KEY_ID} → accessKey</li>
 *   <li>{@code AWS_SECRET_ACCESS_KEY} → secretKey</li>
 * </ul>
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.s3")
public class S3Properties {

    private String bucket;
    private String region = "ap-northeast-2";
    private String accessKey;
    private String secretKey;
}
