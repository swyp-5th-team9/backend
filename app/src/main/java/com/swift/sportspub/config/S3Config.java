package com.swift.sportspub.config;

// TODO [배포 시 주석 해제] S3 인프라 연동
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
// import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
// import software.amazon.awssdk.regions.Region;
// import software.amazon.awssdk.services.s3.S3Client;

/**
 * S3 클라이언트 설정.
 * 로컬 개발 환경에서는 S3 자격 증명 없이도 앱이 기동되도록 비활성화한다.
 * 배포 시 아래 주석을 해제한다.
 */
// @Configuration
public class S3Config {

    // TODO [배포 시 주석 해제] S3 인프라 연동
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
