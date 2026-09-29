package com.swift.sportspub.auth.repository;

import com.swift.sportspub.auth.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByTokenHash(String tokenHash);

    /*
     * uk_refresh_tokens_user 로 user_id당 row가 최대 1개이므로 List가 아닌 Optional을 반환한다.
     * replaceRefreshToken()에서 기존 row 존재 여부에 따라 UPDATE vs INSERT를 분기할 때 사용한다.
     */
    Optional<RefreshToken> findByUserUserId(Long userId);

    void deleteByUserUserId(Long userId);
}
