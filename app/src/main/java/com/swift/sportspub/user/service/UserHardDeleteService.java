package com.swift.sportspub.user.service;

import com.swift.sportspub.user.entity.User;
import com.swift.sportspub.user.repository.UserRepository;
import com.swift.sportspub.user.storage.UserProfileImageStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 탈퇴 보관 기간 경과 회원의 users row 및 연관 데이터 물리 삭제.
 */
@Service
@RequiredArgsConstructor
public class UserHardDeleteService {

    private final UserRepository userRepository;
    private final UserProfileImageStorageService profileImageStorageService;

    @Transactional
    public void hardDelete(User user) {
        profileImageStorageService.deleteByUrlIfPresent(user.getProfileImageUrl());
        userRepository.delete(user);
    }

    @Transactional
    public int hardDeleteExpiredWithdrawn(LocalDateTime threshold) {
        List<User> users = userRepository.findAllWithdrawnBefore(threshold);
        users.forEach(this::hardDelete);
        return users.size();
    }
}
