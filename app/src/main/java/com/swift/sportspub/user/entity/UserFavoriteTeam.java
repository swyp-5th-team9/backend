package com.swift.sportspub.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
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
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "user_favorite_teams",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_user_favorite_teams_user_team",
                columnNames = {"user_id", "team_id"}
        )
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class UserFavoriteTeam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_favorite_team_id")
    private Long userFavoriteTeamId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /*
     * teamId는 Long FK 컬럼으로 보관한다. (#60)
     * MVP: UserService.buildFavoriteTeamResponses()에서 TeamRepository batch 조회;
     * Team row가 없으면 teamName=null로 반환한다(NPE 방어).
     * TODO(#60 보류): @ManyToOne Team 전환 및 fetch join 전략 검토
     */
    @Column(name = "team_id", nullable = false)
    private Long teamId;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    private UserFavoriteTeam(User user, Long teamId) {
        this.user = user;
        this.teamId = teamId;
    }
}
