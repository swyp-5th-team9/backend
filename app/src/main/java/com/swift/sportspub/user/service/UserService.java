package com.swift.sportspub.user.service;

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
}
