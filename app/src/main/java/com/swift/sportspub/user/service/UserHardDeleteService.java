package com.swift.sportspub.user.service;

import com.swift.sportspub.user.entity.FailedProfileImageDeletion;
import com.swift.sportspub.user.entity.User;
import com.swift.sportspub.user.repository.FailedProfileImageDeletionRepository;
import com.swift.sportspub.user.repository.UserRepository;
import com.swift.sportspub.user.storage.UserProfileImageStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 탈퇴 보관 기간 경과 회원의 users row 및 연관 데이터 물리 삭제.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserHardDeleteService {

    private final UserRepository userRepository;
    private final UserProfileImageStorageService profileImageStorageService;
    private final FailedProfileImageDeletionRepository failedProfileImageDeletionRepository;

    @Transactional
    public void hardDelete(User user) {
        Long userId = user.getUserId();
        String profileImageUrl = user.getProfileImageUrl();

        userRepository.delete(user);

        try {
            profileImageStorageService.deleteByUrlIfPresent(profileImageUrl);
        } catch (Exception e) {
            log.warn("프로필 이미지 S3 삭제 실패 — userId={}, url={}", userId, profileImageUrl, e);
            enqueueFailedProfileImageDeletion(profileImageUrl);
        }
    }

    /**
     * Hard Delete 배치 시작 시 S3 삭제 실패 큐를 재시도한다.
     */
    @Transactional
    public int retryFailedProfileImageDeletions() {
        int deletedCount = 0;

        for (FailedProfileImageDeletion pending : failedProfileImageDeletionRepository.findAll()) {
            try {
                profileImageStorageService.deleteByUrlIfPresent(pending.getProfileImageUrl());
                failedProfileImageDeletionRepository.delete(pending);
                deletedCount++;
            } catch (Exception e) {
                log.warn("프로필 이미지 S3 재삭제 실패 — url={}", pending.getProfileImageUrl(), e);
            }
        }

        return deletedCount;
    }

    private void enqueueFailedProfileImageDeletion(String profileImageUrl) {
        if (profileImageUrl == null || profileImageUrl.isBlank()) {
            return;
        }
        if (failedProfileImageDeletionRepository.existsById(profileImageUrl)) {
            return;
        }
        failedProfileImageDeletionRepository.save(new FailedProfileImageDeletion(profileImageUrl));
    }

    @Transactional
    public int hardDeleteExpiredWithdrawn(LocalDateTime threshold) {
        List<User> users = userRepository.findAllWithdrawnBefore(threshold);
        users.forEach(this::hardDelete);
        return users.size();
    }
}
