package com.swift.sportspub.pub.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "pub_supported_teams")
@IdClass(PubSupportedTeamId.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = {"pubId", "teamId"})
public class PubSupportedTeam {

    @Id
    @Column(name = "pub_id", nullable = false)
    private Long pubId;

    @Id
    @Column(name = "team_id", nullable = false)
    private Long teamId;

    @Builder
    private PubSupportedTeam(Long pubId, Long teamId) {
        this.pubId = pubId;
        this.teamId = teamId;
    }
}
