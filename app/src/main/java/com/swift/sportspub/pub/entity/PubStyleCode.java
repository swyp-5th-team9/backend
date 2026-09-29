package com.swift.sportspub.pub.entity;

import lombok.Getter;

@Getter
public enum PubStyleCode {

    BIG_SCREEN("대형 스크린"),
    SINGLE_TV("단일 TV"),
    MULTI_TV("다중 TV"),
    OFFICIAL_PUB("공식 펍"),
    STADIUM_MODE("경기장 모드"),
    SPECTATOR_MODE("관전 모드"),
    LOUD_SPEAKER("사운드 빵빵");

    private final String displayName;

    PubStyleCode(String displayName) {
        this.displayName = displayName;
    }
}
