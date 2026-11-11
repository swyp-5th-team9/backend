package com.swift.sportspub.user.repository;

import com.swift.sportspub.user.entity.UserFcmToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserFcmTokenRepository extends JpaRepository<UserFcmToken, Long> {

    Optional<UserFcmToken> findByToken(String token);

    List<UserFcmToken> findAllByUserUserId(Long userId);

    void deleteByToken(String token);
}
