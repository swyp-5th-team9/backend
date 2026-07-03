package com.swift.sportspub.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * S3 연결 설정 ({@code app.s3.*}).
 *
 * <p>설정 키: {@code app.s3.bucket}, {@code app.s3.region}, {@code app.s3.access-key}, {@code app.s3.secret-key}
 * (로컬은 {@code application-local.yml} / {@code S3_BUCKET}, {@code AWS_REGION} 등 환경 변수 매핑)
 *
 * <p>버킷 퍼블릭 read 등 운영 설정은 {@link com.swift.sportspub.report.storage.ReportS3StorageService} Javadoc 참고.
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
