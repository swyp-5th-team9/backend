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
                name = "uk_refresh_tokens_user",
                columnNames = "user_id"
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

    @Column(name = "token_hash", nullable = false, length = 64)
    private String tokenHash;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Builder
    private RefreshToken(User user, String tokenHash, LocalDateTime expiresAt) {
        this.user = user;
        this.tokenHash = tokenHash;
        this.expiresAt = expiresAt;
    }

    public boolean isExpired() {
        return expiresAt.isBefore(LocalDateTime.now());
    }

    /*
     * refresh_tokens.user_id 에 UNIQUE(uk_refresh_tokens_user) 제약이 있어 사용자당 row는 1개만 존재한다.
     * delete 후 insert 방식은 같은 트랜잭션에서 DELETE가 DB에 반영되기 전 INSERT가 실행되면
     * 동일 user_id로 duplicate key(uk_refresh_tokens_user)가 발생한다.
     * 기존 row의 token_hash·expires_at만 갱신(UPDATE)하면 UNIQUE 충돌 없이 토큰 회전이 가능하다.
     */
    public void rotate(String tokenHash, LocalDateTime expiresAt) {
        this.tokenHash = tokenHash;
        this.expiresAt = expiresAt;
    }
}
