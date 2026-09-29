package com.swift.sportspub.favorite.entity;

import com.swift.sportspub.common.entity.BaseEntity;
import com.swift.sportspub.pub.entity.Pub;
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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pub_id", nullable = false)
    private Pub pub;

    /*
     * pub_id FK read-only 미러 — ERD fk_favorites_pub 와 동일 컬럼.
     * soft-deleted pub는 Pub @SQLRestriction 으로 lazy load 없이 id만 batch 조회할 때 사용한다.
     */
    @Column(name = "pub_id", nullable = false, insertable = false, updatable = false)
    private Long pubId;

    @Builder
    private Favorite(User user, Pub pub) {
        this.user = user;
        this.pub = pub;
    }

    public Long getPubId() {
        if (pubId != null) {
            return pubId;
        }
        return pub != null ? pub.getPubId() : null;
    }
}
