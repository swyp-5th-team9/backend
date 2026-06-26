package com.swift.sportspub.user.repository;

import com.swift.sportspub.user.entity.OAuthProvider;
import com.swift.sportspub.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/*
 * User 조회 정책 (Soft Delete)
 *
 * - findActiveBy*     : deleted_at IS NULL (탈퇴 회원 제외)
 * - *IncludingDeleted : deleted_at 조건 없음 (탈퇴 회원 포함)
 *
 * deleted_at 필터가 필요한 조회는 findActiveBy* 또는 *IncludingDeleted를 명시한다.
 * JpaRepository.findById() 등 기본 메서드는 deleted_at을 구분하지 않으므로 직접 사용하지 않는다.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.userId = :userId AND u.deletedAt IS NULL")
    Optional<User> findActiveById(@Param("userId") Long userId);

    @Query("""
            SELECT u FROM User u
            WHERE u.oauthProvider = :oauthProvider
              AND u.oauthId = :oauthId
            """)
    Optional<User> findByOauthProviderAndOauthIdIncludingDeleted(
            @Param("oauthProvider") OAuthProvider oauthProvider,
            @Param("oauthId") String oauthId
    );
}
