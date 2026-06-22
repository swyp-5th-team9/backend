package com.swift.sportspub.auth.entity;

import com.swift.sportspub.common.entity.BaseEntity;
import com.swift.sportspub.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "refresh_tokens",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_refresh_tokens_token",
                columnNames = "token"
        )
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "refresh_token_id")
    private Long refreshTokenId;

    /*
     * RefreshToken을 User 컬럼이 아닌 별도 테이블로 분리한다.
     *
     * User는 회원 프로필과 계정 상태를 담당하고, RefreshToken은 로그인 세션과 토큰 폐기를 담당한다.
     * 별도 Entity로 두면 한 사용자의 여러 기기 로그인을 표현할 수 있고,
     * 추후 특정 기기 로그아웃, 전체 로그아웃, 토큰 회전 정책을 User 구조 변경 없이 확장할 수 있다.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "token", nullable = false, length = 1000)
    private String token;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "revoked_at")
    private LocalDateTime revokedAt;

    @Builder
    private RefreshToken(User user, String token, LocalDateTime expiresAt, LocalDateTime revokedAt) {
        this.user = user;
        this.token = token;
        this.expiresAt = expiresAt;
        this.revokedAt = revokedAt;
    }

    /*
     * 토큰 row를 삭제하지 않고 폐기 시각을 남긴다.
     * 운영 환경에서는 폐기 이력을 통해 로그아웃, 토큰 탈취 의심, 회전 정책을 추적할 수 있다.
     */
    public void revoke() {
        if (isRevoked()) {
            return;
        }

        this.revokedAt = LocalDateTime.now();
    }

    public boolean isRevoked() {
        return revokedAt != null;
    }

    public boolean isExpired() {
        return expiresAt.isBefore(LocalDateTime.now());
    }
}
