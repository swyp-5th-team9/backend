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
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "users",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_users_oauth",
                columnNames = {"oauth_provider", "oauth_id"}
        )
)
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

    @Column(name = "nickname", length = 20)
    private String nickname;

    @Column(name = "profile_image_url", length = 500)
    private String profileImageUrl;

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
            UserRole role,
            boolean onboardingCompleted,
            LocalDateTime deletedAt
    ) {
        this.oauthProvider = oauthProvider;
        this.oauthId = oauthId;
        this.nickname = nickname;
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

    public void completeOnboarding(String nickname) {
        this.nickname = nickname;
        this.onboardingCompleted = true;
    }

    public void updateProfile(String nickname) {
        if (nickname != null) {
            this.nickname = nickname;
        }
    }

    public void updateProfileImage(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public void changeRole(UserRole role) {
        this.role = role;
    }

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }

    /**
     * 탈퇴 후 보관 기간({@code retentionDays}) 이내이면 계정 복구 가능.
     */
    public boolean canRestore(LocalDateTime now, int retentionDays) {
        if (deletedAt == null) {
            return false;
        }
        return now.isBefore(deletedAt.plusDays(retentionDays));
    }

    /**
     * 탈퇴 후 보관 기간이 경과했는지 여부.
     */
    public boolean isWithdrawalExpired(LocalDateTime now, int retentionDays) {
        if (deletedAt == null) {
            return false;
        }
        return !now.isBefore(deletedAt.plusDays(retentionDays));
    }

    /*
     * 탈퇴 회원이 동일 OAuth 계정으로 재로그인할 때 계정을 복구한다.
     * 보관 기간 이내에만 호출한다. deletedAt만 해제하며 나머지 데이터는 유지한다.
     */
    public void restoreForReLogin() {
        this.deletedAt = null;
    }

    public boolean isDeleted() {
        return deletedAt != null;
    }
}
