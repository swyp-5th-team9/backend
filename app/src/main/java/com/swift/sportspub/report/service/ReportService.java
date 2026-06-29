package com.swift.sportspub.report.service;

import com.swift.sportspub.pub.entity.Pub;
import com.swift.sportspub.pub.repository.PubRepository;
import com.swift.sportspub.report.dto.ReportCreateRequest;
import com.swift.sportspub.report.dto.ReportCreateResponse;
import com.swift.sportspub.report.entity.Report;
import com.swift.sportspub.report.entity.ReportImage;
import com.swift.sportspub.report.exception.ReportErrorCode;
import com.swift.sportspub.report.exception.ReportException;
import com.swift.sportspub.report.repository.ReportImageRepository;
import com.swift.sportspub.report.repository.ReportRepository;
// TODO [S3 배포 시 주석 해제] 3단계: ReportS3StorageService 주입
// import com.swift.sportspub.report.storage.ReportS3StorageService;
import com.swift.sportspub.user.entity.User;
import com.swift.sportspub.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

/**
 * 제보 등록 서비스.
 *
 * <p>[S3 배포 시 변경 체크리스트]
 * <ol>
 *   <li>{@code S3Config} — {@code @Configuration}, {@code S3Client} 빈 주석 해제</li>
 *   <li>{@code ReportS3StorageService} — {@code @Service} 및 upload 로직 주석 해제</li>
 *   <li>환경 변수 — {@code S3_BUCKET}, {@code AWS_ACCESS_KEY_ID}, {@code AWS_SECRET_ACCESS_KEY} 설정
 *       ({@code .env} / {@code application-local.yml} 의 {@code app.s3.*})</li>
 *   <li>이 클래스 — 아래 {@code reportS3StorageService} 주입·{@code upload()} 호출로 교체,
 *       {@code buildLocalImageUrl()} 및 {@code LOCAL_IMAGE_URL_PREFIX} 제거</li>
 * </ol>
 *
 * <p>Controller, {@link ReportCreateRequest}, {@code createReport(userId, request)} 시그니처는 변경하지 않는다.
 */
@Service
@RequiredArgsConstructor
public class ReportService {

    private static final int MAX_IMAGES = 3;
    private static final long MAX_IMAGE_SIZE_BYTES = 10L * 1024 * 1024;

    private static final Set<String> ALLOWED_IMAGE_CONTENT_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/gif",
            "image/webp"
    );

    private static final Set<String> ALLOWED_IMAGE_EXTENSIONS = Set.of(
            "jpg", "jpeg", "png", "gif", "webp"
    );

    // TODO [S3 배포 시 제거] 로컬 개발용 placeholder URL prefix
    private static final String LOCAL_IMAGE_URL_PREFIX = "local://reports/";

    private final UserService userService;
    private final PubRepository pubRepository;
    private final ReportRepository reportRepository;
    private final ReportImageRepository reportImageRepository;
    // TODO [S3 배포 시 주석 해제] 3단계: 아래 필드 주입 후 buildLocalImageUrl 대신 upload() 사용
    // private final ReportS3StorageService reportS3StorageService;

    @Transactional
    public ReportCreateResponse createReport(Long userId, ReportCreateRequest request) {
        User user = userService.getUser(userId);
        Pub pub = resolvePub(request.getPubId());
        List<MultipartFile> images = filterImages(request.getImages());
        validateImages(images);

        Report report = reportRepository.save(
                Report.builder()
                        .user(user)
                        .pub(pub)
                        .category(request.getCategory())
                        .content(request.getContent())
                        .build()
        );

        for (MultipartFile image : images) {
            // TODO [S3 배포 시] buildLocalImageUrl() 제거 후 아래 한 줄만 사용
            String imageUrl = buildLocalImageUrl(image);
            // String imageUrl = reportS3StorageService.upload(image);
            reportImageRepository.save(
                    ReportImage.builder()
                            .report(report)
                            .imageUrl(imageUrl)
                            .build()
            );
        }

        return ReportCreateResponse.from(report);
    }

    // TODO [S3 배포 시 제거] 로컬 개발용 — S3 연동 후 ReportS3StorageService.upload()로 대체
    private String buildLocalImageUrl(MultipartFile file) {
        String filename = file.getOriginalFilename();
        if (filename == null || filename.isBlank()) {
            filename = "image";
        }
        return LOCAL_IMAGE_URL_PREFIX + UUID.randomUUID() + "-" + filename.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    private Pub resolvePub(Long pubId) {
        if (pubId == null) {
            return null;
        }

        return pubRepository.findById(pubId)
                .orElseThrow(() -> new ReportException(ReportErrorCode.PUB_NOT_FOUND));
    }

    private List<MultipartFile> filterImages(List<MultipartFile> images) {
        if (images == null || images.isEmpty()) {
            return List.of();
        }

        List<MultipartFile> filtered = new ArrayList<>();
        for (MultipartFile image : images) {
            if (image != null && !image.isEmpty()) {
                filtered.add(image);
            }
        }
        return filtered;
    }

    private void validateImages(List<MultipartFile> images) {
        if (images.size() > MAX_IMAGES) {
            throw new ReportException(ReportErrorCode.REPORT_IMAGE_LIMIT_EXCEEDED);
        }

        for (MultipartFile image : images) {
            if (image.getSize() > MAX_IMAGE_SIZE_BYTES) {
                throw new ReportException(ReportErrorCode.REPORT_IMAGE_SIZE_EXCEEDED);
            }
            if (!isSupportedImage(image)) {
                throw new ReportException(ReportErrorCode.UNSUPPORTED_IMAGE_FORMAT);
            }
        }
    }

    private boolean isSupportedImage(MultipartFile file) {
        String contentType = file.getContentType();
        if (StringUtils.hasText(contentType) && ALLOWED_IMAGE_CONTENT_TYPES.contains(contentType.toLowerCase(Locale.ROOT))) {
            return true;
        }

        String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
        return StringUtils.hasText(extension)
                && ALLOWED_IMAGE_EXTENSIONS.contains(extension.toLowerCase(Locale.ROOT));
    }
}
