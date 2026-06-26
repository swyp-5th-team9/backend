package com.swift.sportspub.favorite.repository;

import com.swift.sportspub.favorite.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
}
