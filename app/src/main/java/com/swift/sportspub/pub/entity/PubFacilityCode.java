package com.swift.sportspub.pub.entity;

import lombok.Getter;

@Getter
public enum PubFacilityCode {

    GROUP_SEAT("단체석"),
    COUNTER_SEAT("카운터석"),
    TERRACE("테라스"),
    SOLO_SEAT("1인석"),
    SPACIOUS_AREA("넓은 공간"),
    ROOFTOP("루프탑"),
    PARKING("주차"),
    VALET_PARKING("발렛파킹"),
    PET_FRIENDLY("반려동물 동반"),
    PRIVATE_BOOKING("단체 대관"),
    WHEELCHAIR_ACCESS("휠체어 접근"),
    OUTDOOR_SEAT("야외좌석"),
    RESERVATION("예약가능");

    private final String displayName;

    PubFacilityCode(String displayName) {
        this.displayName = displayName;
    }
}
