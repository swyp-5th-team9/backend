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

import java.time.LocalTime;

@Entity
@Table(name = "pub_business_hours")
@IdClass(PubBusinessHoursId.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = {"pubId", "dayOfWeek"})
public class PubBusinessHours {

    @Id
    @Column(name = "pub_id", nullable = false)
    private Long pubId;

    @Id
    @Column(name = "day_of_week", nullable = false)
    private Short dayOfWeek;

    @Column(name = "open_time")
    private LocalTime openTime;

    @Column(name = "close_time")
    private LocalTime closeTime;

    @Column(name = "is_closed", nullable = false)
    private Boolean isClosed;

    @Builder
    private PubBusinessHours(Long pubId, Short dayOfWeek, LocalTime openTime, LocalTime closeTime, Boolean isClosed) {
        this.pubId = pubId;
        this.dayOfWeek = dayOfWeek;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.isClosed = isClosed != null ? isClosed : Boolean.FALSE;
    }
}
