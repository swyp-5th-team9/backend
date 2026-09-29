package com.swift.sportspub.team.entity;

import com.swift.sportspub.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "teams",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_teams_sport_name",
                        columnNames = {"sport_type", "name"}
                ),
                @UniqueConstraint(
                        name = "uk_teams_sport_short",
                        columnNames = {"sport_type", "short_name"}
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "teamId", callSuper = false)
public class Team extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_id")
    private Long teamId;

    @Enumerated(EnumType.STRING)
    @Column(name = "sport_type", nullable = false, length = 20)
    private SportType sportType;

    @Column(name = "name", nullable = false, length = 30)
    private String name;

    @Column(name = "short_name", nullable = false, length = 10)
    private String shortName;

    @Column(name = "home_stadium", length = 50)
    private String homeStadium;

    @Builder
    private Team(SportType sportType, String name, String shortName, String homeStadium) {
        this.sportType = sportType != null ? sportType : SportType.KBO;
        this.name = name;
        this.shortName = shortName;
        this.homeStadium = homeStadium;
    }
}
