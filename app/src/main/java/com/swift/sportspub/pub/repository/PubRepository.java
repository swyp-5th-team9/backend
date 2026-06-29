package com.swift.sportspub.pub.repository;

import com.swift.sportspub.pub.dto.PubMapMarker;
import com.swift.sportspub.pub.entity.Pub;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface PubRepository extends JpaRepository<Pub, Long>, PubRepositoryCustom {

    @Query("""
            SELECT new com.swift.sportspub.pub.dto.PubMapMarker(
                p.pubId, p.name, p.latitude, p.longitude, p.status, p.favoriteCount
            )
            FROM Pub p
            WHERE p.latitude BETWEEN :swLat AND :neLat
              AND p.longitude BETWEEN :swLng AND :neLng
              AND (:teamId IS NULL OR EXISTS (
                  SELECT 1 FROM PubSupportedTeam pst
                  WHERE pst.pubId = p.pubId AND pst.teamId = :teamId
              ))
            """)
    List<PubMapMarker> findMarkersInBoundingBox(
            @Param("swLat") BigDecimal swLat,
            @Param("swLng") BigDecimal swLng,
            @Param("neLat") BigDecimal neLat,
            @Param("neLng") BigDecimal neLng,
            @Param("teamId") Long teamId
    );
}
