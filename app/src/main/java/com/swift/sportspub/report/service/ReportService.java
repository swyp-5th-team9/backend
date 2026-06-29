package com.swift.sportspub.report.service;

import com.swift.sportspub.common.exception.BusinessException;
import com.swift.sportspub.common.exception.ErrorCode;
import com.swift.sportspub.pub.entity.Pub;
import com.swift.sportspub.pub.repository.PubRepository;
import com.swift.sportspub.report.dto.ReportCreateRequest;
import com.swift.sportspub.report.dto.ReportCreateResponse;
import com.swift.sportspub.report.entity.Report;
import com.swift.sportspub.report.entity.ReportImage;
import com.swift.sportspub.report.repository.ReportImageRepository;
import com.swift.sportspub.report.repository.ReportRepository;
import com.swift.sportspub.report.storage.ReportS3StorageService;
import com.swift.sportspub.user.entity.User;
import com.swift.sportspub.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private static final int MAX_IMAGES = 3;

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
        validateImageCount(images);

        Report report = reportRepository.save(
                Report.builder()
                        .user(user)
                        .pub(pub)
                        .category(request.getCategory())
                        .subcategory(request.getSubcategory())
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
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "존재하지 않는 pubId입니다."));
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

    private void validateImageCount(List<MultipartFile> images) {
        if (images.size() > MAX_IMAGES) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "이미지는 최대 3장까지 첨부할 수 있습니다.");
        }
    }
}
