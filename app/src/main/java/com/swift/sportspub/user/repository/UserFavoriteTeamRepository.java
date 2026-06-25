package com.swift.sportspub.user.repository;

import com.swift.sportspub.user.entity.UserFavoriteTeam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserFavoriteTeamRepository extends JpaRepository<UserFavoriteTeam, Long> {

    @Modifying(flushAutomatically = true)
    @Query("delete from UserFavoriteTeam uft where uft.user.userId = :userId")
    void deleteByUserId(@Param("userId") Long userId);
}
