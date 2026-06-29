package com.swift.sportspub.pub.repository;

import com.swift.sportspub.pub.entity.PubSupportedTeam;
import com.swift.sportspub.pub.entity.PubSupportedTeamId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface PubSupportedTeamRepository extends JpaRepository<PubSupportedTeam, PubSupportedTeamId> {

    @Query("""
            SELECT pst.pubId AS pubId, t.teamId AS teamId, t.shortName AS shortName, t.name AS name
            FROM PubSupportedTeam pst
            JOIN com.swift.sportspub.team.entity.Team t ON t.teamId = pst.teamId
            WHERE pst.pubId IN :pubIds
            ORDER BY pst.pubId ASC, t.teamId ASC
            """)
    List<SupportedTeamRow> findAllRowsByPubIdIn(@Param("pubIds") Collection<Long> pubIds);

    interface SupportedTeamRow {
        Long getPubId();
        Long getTeamId();
        String getShortName();
        String getName();
    }
}
