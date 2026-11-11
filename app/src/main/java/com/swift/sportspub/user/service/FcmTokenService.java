package com.swift.sportspub.user.service;

import com.swift.sportspub.user.dto.FcmTokenRequest;
import com.swift.sportspub.user.entity.User;
import com.swift.sportspub.user.entity.UserFcmToken;
import com.swift.sportspub.user.exception.UserNotFoundException;
import com.swift.sportspub.user.repository.UserFcmTokenRepository;
import com.swift.sportspub.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FcmTokenService {

    private final UserRepository userRepository;
    private final UserFcmTokenRepository userFcmTokenRepository;

    @Transactional
    public void register(Long userId, FcmTokenRequest request) {
        User user = userRepository.findActiveById(userId)
                .orElseThrow(UserNotFoundException::new);

        String token = request.token().trim();
        userFcmTokenRepository.findByToken(token).ifPresentOrElse(
                existing -> {
                    if (!existing.getUser().getUserId().equals(userId)) {
                        existing.reassignTo(user);
                    }
                },
                () -> userFcmTokenRepository.save(UserFcmToken.builder()
                        .user(user)
                        .token(token)
                        .build())
        );
    }
}
