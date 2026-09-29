package com.swift.sportspub.pub.entity;

import lombok.Getter;

@Getter
public enum SubRegion {

    JAMSIL(Region.SONGPA, "잠실/잠실새내"),
    HONGDAE_HAPJEONG(Region.MAPO, "홍대/합정"),
    SANGAM_MANGWON(Region.MAPO, "상암/망원");

    private final Region region;
    private final String displayName;

    SubRegion(Region region, String displayName) {
        this.region = region;
        this.displayName = displayName;
    }
}
