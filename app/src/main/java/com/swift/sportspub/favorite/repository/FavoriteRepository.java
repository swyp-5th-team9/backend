package com.swift.sportspub.favorite.repository;

import com.swift.sportspub.favorite.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    List<Favorite> findTop30ByUserUserIdOrderByCreatedAtDesc(Long userId);

    List<Favorite> findByFavoriteIdInAndUserUserId(Collection<Long> favoriteIds, Long userId);
}
