package com.swift.sportspub.user.service;

import com.swift.sportspub.user.dto.OnboardingRequest;
import com.swift.sportspub.user.dto.UserResponse;
import com.swift.sportspub.user.entity.User;
import com.swift.sportspub.user.exception.UserNotFoundException;
import com.swift.sportspub.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /*
     * 현재 JWT 인증 구조는 SecurityContext principal에 Long userId를 저장한다.
     * 후속 User API(#11~#13)는 이 메서드를 통해 인증된 사용자를 일관되게 조회한다.
     */
    @Transactional(readOnly = true)
    public User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public UserResponse getMyInfo(Long userId) {
        User user = getUser(userId);
        return toUserResponse(user);
    }

    /*
     * teamIds는 현재 최대 개수 검증만 수행한다.
     * 선호 구단 저장은 #12에서 Team/UserFavoriteTeam 구조를 만들 때 함께 연결한다.
     */
    @Transactional
    public void onboarding(Long userId, OnboardingRequest request) {
        User user = getUser(userId);
        user.completeOnboarding(request.nickname());
    }

    private UserResponse toUserResponse(User user) {
        return new UserResponse(
                user.getUserId(),
                user.getNickname(),
                user.getRole(),
                user.isOnboardingCompleted(),
                List.of()
        );
    }
}
