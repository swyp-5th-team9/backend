package com.swift.sportspub.favorite.entity;

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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "favorites",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_favorites_user_pub",
                columnNames = {"user_id", "pub_id"}
        )
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "favoriteId", callSuper = false)
public class Favorite extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "favorite_id")
    private Long favoriteId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /*
     * pubId는 Long FK 컬럼으로 보관한다. (#60)
     * MVP: FavoriteService에서 PubRepository·PubImageRepository batch 조회로 N+1을 방지한다.
     * soft-deleted pub는 Pub @SQLRestriction으로 제외되며, 목록 API는 pubName·region·thumbnail=null로 반환한다.
     * TODO(#60 보류): @ManyToOne Pub 전환 및 fetch join 전략 검토
     */
    @Column(name = "pub_id", nullable = false)
    private Long pubId;

    @Builder
    private Favorite(User user, Long pubId) {
        this.user = user;
        this.pubId = pubId;
    }
}
