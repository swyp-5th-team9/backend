package com.swift.sportspub.team.repository;

import com.swift.sportspub.team.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Long> {
}
