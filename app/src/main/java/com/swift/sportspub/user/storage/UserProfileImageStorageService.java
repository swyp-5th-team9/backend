package com.swift.sportspub.user.storage;

import com.swift.sportspub.config.S3Properties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

/**
 * 회원 프로필 이미지 S3 업로드 서비스.
 *
 * <p><b>운영 설정 필요 (인프라 — 애플리케이션 코드 변경 아님)</b>
 * 업로드({@code putObject})는 private 버킷에서도 성공하지만, 저장되는 URL은 퍼블릭 GET을 가정한다.
 * 버킷/객체가 private이면 브라우저에서 {@code AccessDenied}가 난다 (로컬·운영 동일).
 * <ul>
 *   <li>S3 콘솔 → 버킷 → {@code profiles/} prefix 객체 업로드 여부 확인</li>
 *   <li>버킷 정책: {@code arn:aws:s3:::{bucket}/profiles/*} 에 {@code s3:GetObject} 허용
 *       (또는 CloudFront·Presigned URL 등 별도 설계)</li>
 *   <li>Block Public Access 설정과 정책 충돌 여부 확인</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class UserProfileImageStorageService {

    private static final String PROFILE_IMAGE_PREFIX = "profiles/";

    private final S3Client s3Client;
    private final S3Properties s3Properties;

    public String upload(MultipartFile file) {
        String key = PROFILE_IMAGE_PREFIX + UUID.randomUUID() + "-" + sanitizeFilename(file.getOriginalFilename());

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
     * Hard Delete 시 저장된 프로필 이미지 객체를 삭제한다. URL이 현재 버킷과 맞지 않으면 무시한다.
     */
    public void deleteByUrlIfPresent(String profileImageUrl) {
        if (profileImageUrl == null || profileImageUrl.isBlank()) {
            return;
        }

        String prefix = "https://%s.s3.%s.amazonaws.com/".formatted(
                s3Properties.getBucket(),
                s3Properties.getRegion()
        );
        if (!profileImageUrl.startsWith(prefix)) {
            return;
        }

        String key = profileImageUrl.substring(prefix.length());
        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(s3Properties.getBucket())
                .key(key)
                .build());
    }

    /**
     * 퍼블릭 S3 URL 문자열을 반환한다. 실제 조회 가능 여부는 AWS 버킷 정책/ACL에 따른다.
     *
     * @see #upload(MultipartFile) 클래스 Javadoc — 운영 설정 필요
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
