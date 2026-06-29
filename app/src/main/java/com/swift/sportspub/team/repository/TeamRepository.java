package com.swift.sportspub.team.repository;

import com.swift.sportspub.team.entity.SportType;
import com.swift.sportspub.team.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamRepository extends JpaRepository<Team, Long> {

    List<Team> findAllBySportTypeOrderByTeamIdAsc(SportType sportType);
}
