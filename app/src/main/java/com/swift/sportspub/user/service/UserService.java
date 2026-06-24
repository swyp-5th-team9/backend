package com.swift.sportspub.user.service;

import com.swift.sportspub.user.dto.OnboardingRequest;
import com.swift.sportspub.user.entity.User;
import com.swift.sportspub.user.exception.UserNotFoundException;
import com.swift.sportspub.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    /*
     * 현재 Team 도메인이 구현되어 있지 않아 teamIds는 요청 검증만 수행하고 저장하지 않는다.
     * 선호 구단 저장은 Team/UserFavoriteTeam 구조가 준비된 후 별도 이슈에서 연결한다.
     */
    @Transactional
    public void onboarding(Long userId, OnboardingRequest request) {
        User user = getUser(userId);
        user.completeOnboarding(request.nickname());
    }
}
