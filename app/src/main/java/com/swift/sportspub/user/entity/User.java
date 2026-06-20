package com.swift.sportspub.user.entity;

import com.swift.sportspub.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@SQLRestriction("deleted_at IS NULL")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "userId", callSuper = false)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "oauth_provider", nullable = false, length = 20)
    private OAuthProvider oauthProvider;

    @Column(name = "oauth_id", nullable = false, length = 100)
    private String oauthId;

    @Column(name = "nickname", length = 30)
    private String nickname;

    @Column(name = "favorite_team_id")
    private Long favoriteTeamId;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private UserRole role;

    @Column(name = "onboarding_completed", nullable = false)
    private boolean onboardingCompleted;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Builder
    private User(
            OAuthProvider oauthProvider,
            String oauthId,
            String nickname,
            Long favoriteTeamId,
            UserRole role,
            boolean onboardingCompleted,
            LocalDateTime deletedAt
    ) {
        this.oauthProvider = oauthProvider;
        this.oauthId = oauthId;
        this.nickname = nickname;
        this.favoriteTeamId = favoriteTeamId;
        this.role = role != null ? role : UserRole.FAN;
        this.onboardingCompleted = onboardingCompleted;
        this.deletedAt = deletedAt;
    }

    public static User createOAuthUser(OAuthProvider oauthProvider, String oauthId) {
        return User.builder()
                .oauthProvider(oauthProvider)
                .oauthId(oauthId)
                .role(UserRole.FAN)
                .onboardingCompleted(false)
                .build();
    }

    public void completeOnboarding(String nickname, Long favoriteTeamId) {
        this.nickname = nickname;
        this.favoriteTeamId = favoriteTeamId;
        this.onboardingCompleted = true;
    }

    public void updateProfile(String nickname, Long favoriteTeamId) {
        if (nickname != null) {
            this.nickname = nickname;
        }
        if (favoriteTeamId != null) {
            this.favoriteTeamId = favoriteTeamId;
        }
    }

    public void changeRole(UserRole role) {
        this.role = role;
    }

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }

    public boolean isDeleted() {
        return deletedAt != null;
    }
}
