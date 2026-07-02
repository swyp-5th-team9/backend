package com.swift.sportspub.report.storage;

import com.swift.sportspub.config.S3Properties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

/**
 * 제보 이미지 S3 업로드 서비스.
 *
 * <p>Controller, {@link com.swift.sportspub.report.dto.ReportCreateRequest}, multipart API 계약은 변경하지 않는다.
 *
 * <p><b>[AWS 버킷 설정 TODO — URL로 이미지 조회 시 필수]</b>
 * 업로드({@code putObject})는 private 버킷에서도 성공하지만, 저장되는 URL은 퍼블릭 GET을 가정한다.
 * 버킷/객체가 private이면 브라우저에서 {@code AccessDenied}가 난다 (로컬·운영 동일).
 * <ul>
 *   <li>S3 콘솔 → 버킷 → {@code reports/} prefix 객체 업로드 여부 확인</li>
 *   <li>버킷 정책: {@code arn:aws:s3:::{bucket}/reports/*} 에 {@code s3:GetObject} 허용
 *       (또는 CloudFront·Presigned URL 등 별도 설계)</li>
 *   <li>Block Public Access 설정과 정책 충돌 여부 확인</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class ReportS3StorageService {

    private static final String REPORT_IMAGE_PREFIX = "reports/";

    private final S3Client s3Client;
    private final S3Properties s3Properties;

    public String upload(MultipartFile file) {
        String key = REPORT_IMAGE_PREFIX + UUID.randomUUID() + "-" + sanitizeFilename(file.getOriginalFilename());

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(s3Properties.getBucket())
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));
        } catch (IOException e) {
            throw new IllegalStateException("이미지 업로드에 실패했습니다.", e);
        }

        return buildPublicUrl(key);
    }

    /**
     * 퍼블릭 S3 URL 문자열을 반환한다. 실제 조회 가능 여부는 AWS 버킷 정책/ACL에 따른다.
     *
     * @see #upload(MultipartFile) 클래스 Javadoc — [AWS 버킷 설정 TODO]
     */
    private String buildPublicUrl(String key) {
        return "https://%s.s3.%s.amazonaws.com/%s".formatted(
                s3Properties.getBucket(),
                s3Properties.getRegion(),
                key
        );
    }

    private String sanitizeFilename(String filename) {
        if (filename == null || filename.isBlank()) {
            return "image";
        }
        return filename.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}
