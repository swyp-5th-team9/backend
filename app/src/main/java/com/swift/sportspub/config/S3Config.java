package com.swift.sportspub.config;

// TODO [S3 배포 시 주석 해제] 1단계: S3Client 빈 등록
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
// import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
// import software.amazon.awssdk.regions.Region;
// import software.amazon.awssdk.services.s3.S3Client;

/**
 * S3 클라이언트 설정.
 *
 * <p>로컬 개발 환경에서는 S3 자격 증명 없이 앱이 기동되도록 비활성화한다.
 *
 * <p>[S3 배포 시]
 * <ol>
 *   <li>이 클래스의 {@code @Configuration}, {@code s3Client} 빈 주석 해제</li>
 *   <li>{@link S3Properties} 에 bucket/region/accessKey/secretKey 값 설정
 *       (환경 변수: {@code S3_BUCKET}, {@code AWS_REGION}, {@code AWS_ACCESS_KEY_ID}, {@code AWS_SECRET_ACCESS_KEY})</li>
 *   <li>{@code ReportS3StorageService} 주석 해제 후 {@code ReportService}에서 주입·사용</li>
 * </ol>
 *
 * <p>Controller, DTO, API 명세는 변경하지 않는다.
 */
// @Configuration
public class S3Config {

    // TODO [S3 배포 시 주석 해제] 1단계
    // @Bean
    // public S3Client s3Client(S3Properties s3Properties) {
    //     return S3Client.builder()
    //             .region(Region.of(s3Properties.getRegion()))
    //             .credentialsProvider(StaticCredentialsProvider.create(
    //                     AwsBasicCredentials.create(
    //                             s3Properties.getAccessKey(),
    //                             s3Properties.getSecretKey()
    //                     )
    //             ))
    //             .build();
    // }
}
