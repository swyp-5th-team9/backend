package com.swift.sportspub.report.storage;

// TODO [S3 배포 시 주석 해제] 2단계: S3 업로드 서비스 활성화
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
 *
 * <p>로컬 개발 환경에서는 {@link com.swift.sportspub.config.S3Config} 가 비활성화되어
 * S3Client 빈이 없으므로 이 클래스도 비활성화한다.
 * 로컬에서는 {@link com.swift.sportspub.report.service.ReportService#buildLocalImageUrl} 가
 * placeholder URL을 생성한다.
 *
 * <p>[S3 배포 시]
 * <ol>
 *   <li>{@code S3Config} 주석 해제 (선행 조건)</li>
 *   <li>이 클래스의 {@code @Service}, {@code upload()} 등 전체 구현 주석 해제</li>
 *   <li>{@code ReportService} — {@code reportS3StorageService.upload(image)} 사용,
 *       {@code buildLocalImageUrl()} 제거</li>
 * </ol>
 *
 * <p>Controller, {@link com.swift.sportspub.report.dto.ReportCreateRequest}, multipart API 계약은 변경하지 않는다.
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
