package com.swift.sportspub.team.service;

import com.swift.sportspub.team.dto.TeamListResponse;
import com.swift.sportspub.team.dto.TeamResponse;
import com.swift.sportspub.team.entity.SportType;
import com.swift.sportspub.team.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;

    @Transactional(readOnly = true)
    public TeamListResponse getList(SportType sportType) {
        SportType resolved = sportType != null ? sportType : SportType.KBO;
        List<TeamResponse> teams = teamRepository
                .findAllBySportTypeOrderByTeamIdAsc(resolved)
                .stream()
                .map(TeamResponse::from)
                .toList();
        return TeamListResponse.of(teams);
    }
}
