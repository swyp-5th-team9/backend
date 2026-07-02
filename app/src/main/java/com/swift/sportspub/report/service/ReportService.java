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
import com.swift.sportspub.report.storage.ReportS3StorageService;
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

/**
 * 제보 등록 서비스.
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

    private final UserService userService;
    private final PubRepository pubRepository;
    private final ReportRepository reportRepository;
    private final ReportImageRepository reportImageRepository;
    private final ReportS3StorageService reportS3StorageService;

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
            String imageUrl = reportS3StorageService.upload(image);
            reportImageRepository.save(
                    ReportImage.builder()
                            .report(report)
                            .imageUrl(imageUrl)
                            .build()
            );
        }

        return ReportCreateResponse.from(report);
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
