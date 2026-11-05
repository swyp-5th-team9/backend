package com.swift.sportspub.user.repository;

import com.swift.sportspub.user.entity.User;
import com.swift.sportspub.user.entity.UserFavoriteTeam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserFavoriteTeamRepository extends JpaRepository<UserFavoriteTeam, Long> {

    @Query("""
            SELECT uft FROM UserFavoriteTeam uft
            JOIN FETCH uft.team
            WHERE uft.user.userId = :userId
            ORDER BY uft.createdAt ASC
            """)
    List<UserFavoriteTeam> findByUserUserIdOrderByCreatedAtAsc(@Param("userId") Long userId);

    @Modifying(flushAutomatically = true)
    @Query("delete from UserFavoriteTeam uft where uft.user.userId = :userId")
    void deleteByUserId(@Param("userId") Long userId);

    @Query("""
            SELECT DISTINCT uft.user FROM UserFavoriteTeam uft
            WHERE uft.user.deletedAt IS NULL
              AND (uft.team.teamId = :homeTeamId OR uft.team.teamId = :awayTeamId)
            """)
    List<User> findActiveUsersSupportingTeams(
            @Param("homeTeamId") Long homeTeamId,
            @Param("awayTeamId") Long awayTeamId
    );
}
