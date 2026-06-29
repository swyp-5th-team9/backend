package com.swift.sportspub.favorite.repository;

import com.swift.sportspub.favorite.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    List<Favorite> findTop30ByUserUserIdOrderByCreatedAtDesc(Long userId);
    List<Favorite> findByFavoriteIdInAndUserUserId(Collection<Long> favoriteIds, Long userId);


    boolean existsByUserUserIdAndPubId(Long userId, Long pubId);

    long countByUserUserId(Long userId);

    @Query(value = """
            SELECT EXISTS (
                SELECT 1
                FROM pubs p
                WHERE p.pub_id = :pubId
                  AND p.deleted_at IS NULL
            )
            """, nativeQuery = true)
    boolean existsActivePub(@Param("pubId") Long pubId);


}
