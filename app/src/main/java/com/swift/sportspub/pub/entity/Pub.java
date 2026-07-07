package com.swift.sportspub.pub.entity;

import com.swift.sportspub.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pubs")
@SQLRestriction("deleted_at IS NULL")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "pubId", callSuper = false)
public class Pub extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pub_id")
    private Long pubId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "address", nullable = false, length = 200)
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(name = "region", nullable = false, length = 30)
    private Region region;

    @Enumerated(EnumType.STRING)
    @Column(name = "sub_region", length = 30)
    private SubRegion subRegion;

    @Column(name = "latitude", nullable = true, precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(name = "longitude", nullable = true, precision = 10, scale = 7)
    private BigDecimal longitude;

    @Column(name = "phone", length = 20)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private PubStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "capacity_range", length = 20)
    private CapacityRange capacityRange;

    @Column(name = "group_seat_max_people")
    private Integer groupSeatMaxPeople;

    @Column(name = "favorite_count", nullable = false)
    private Integer favoriteCount;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Builder
    private Pub(String name, String address, Region region, BigDecimal latitude, BigDecimal longitude,
                String phone, PubStatus status, CapacityRange capacityRange,
                Integer groupSeatMaxPeople, String description) {
        this.name = name;
        this.address = address;
        this.region = region;
        this.latitude = latitude;
        this.longitude = longitude;
        this.phone = phone;
        this.status = status != null ? status : PubStatus.OPEN;
        this.capacityRange = capacityRange;
        this.groupSeatMaxPeople = groupSeatMaxPeople;
        this.favoriteCount = 0;
        this.description = description;
    }
}
