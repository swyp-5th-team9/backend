package com.swift.sportspub.pub.repository;

import com.swift.sportspub.pub.entity.PubSupportedTeam;
import com.swift.sportspub.pub.entity.PubSupportedTeamId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PubSupportedTeamRepository extends JpaRepository<PubSupportedTeam, PubSupportedTeamId> {
}
