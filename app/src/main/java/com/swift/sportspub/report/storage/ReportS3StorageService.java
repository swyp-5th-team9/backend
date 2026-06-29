package com.swift.sportspub.report.storage;

// TODO [배포 시 주석 해제] S3 인프라 연동
// import com.swift.sportspub.config.S3Properties;
// import lombok.RequiredArgsConstructor;
// import org.springframework.stereotype.Service;
// import org.springframework.web.multipart.MultipartFile;
// import software.amazon.awssdk.core.sync.RequestBody;
// import software.amazon.awssdk.services.s3.S3Client;
// import software.amazon.awssdk.services.s3.model.PutObjectRequest;
//
// import java.io.IOException;
// import java.util.UUID;

/**
 * 제보 이미지 S3 업로드 서비스.
 * 로컬 개발 환경에서는 S3Client 빈이 없으므로 비활성화한다.
 * 배포 시 아래 주석을 해제하고 ReportService의 로컬 업로드 대체 로직을 제거한다.
 */
// @Service
// @RequiredArgsConstructor
public class ReportS3StorageService {

    // private static final String REPORT_IMAGE_PREFIX = "reports/";
    //
    // private final S3Client s3Client;
    // private final S3Properties s3Properties;
    //
    // public String upload(MultipartFile file) {
    //     String key = REPORT_IMAGE_PREFIX + UUID.randomUUID() + "-" + sanitizeFilename(file.getOriginalFilename());
    //
    //     try {
    //         PutObjectRequest putObjectRequest = PutObjectRequest.builder()
    //                 .bucket(s3Properties.getBucket())
    //                 .key(key)
    //                 .contentType(file.getContentType())
    //                 .build();
    //
    //         s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));
    //     } catch (IOException e) {
    //         throw new IllegalStateException("이미지 업로드에 실패했습니다.", e);
    //     }
    //
    //     return buildPublicUrl(key);
    // }
    //
    // private String buildPublicUrl(String key) {
    //     return "https://%s.s3.%s.amazonaws.com/%s".formatted(
    //             s3Properties.getBucket(),
    //             s3Properties.getRegion(),
    //             key
    //     );
    // }
    //
    // private String sanitizeFilename(String filename) {
    //     if (filename == null || filename.isBlank()) {
    //         return "image";
    //     }
    //     return filename.replaceAll("[^a-zA-Z0-9._-]", "_");
    // }
}
