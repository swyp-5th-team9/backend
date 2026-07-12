package com.swift.sportspub.pub.repository;

import com.swift.sportspub.pub.entity.Pub;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PubRepository extends JpaRepository<Pub, Long>, PubRepositoryCustom {

    // favorite 도메인에서 찜 등록/해제 시 호출. dirty checking 대신 원자적 UPDATE 로 동시성 확보.
    // pubs.favorite_count CHECK(>= 0) 제약 방어를 위해 감소는 하한 0 clamp.
    @Modifying(flushAutomatically = true)
    @Query("update Pub p set p.favoriteCount = p.favoriteCount + 1 where p.pubId = :pubId")
    int incrementFavoriteCount(@Param("pubId") Long pubId);

    @Modifying(flushAutomatically = true)
    @Query("update Pub p set p.favoriteCount = case when p.favoriteCount > 0 then p.favoriteCount - 1 else 0 end where p.pubId = :pubId")
    int decrementFavoriteCount(@Param("pubId") Long pubId);
}
