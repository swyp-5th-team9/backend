package com.swift.sportspub.pub.entity;

import lombok.Getter;

@Getter
public enum Region {

    GANGNAM(Metro.SEOUL, "강남구"),
    GANGDONG(Metro.SEOUL, "강동구"),
    GANGBUK(Metro.SEOUL, "강북구"),
    GANGSEO(Metro.SEOUL, "강서구"),
    GWANAK(Metro.SEOUL, "관악구"),
    GWANGJIN(Metro.SEOUL, "광진구"),
    GURO(Metro.SEOUL, "구로구"),
    GEUMCHEON(Metro.SEOUL, "금천구"),
    NOWON(Metro.SEOUL, "노원구"),
    DOBONG(Metro.SEOUL, "도봉구"),
    DONGDAEMUN(Metro.SEOUL, "동대문구"),
    DONGJAK(Metro.SEOUL, "동작구"),
    MAPO(Metro.SEOUL, "마포구"),
    SEODAEMUN(Metro.SEOUL, "서대문구"),
    SEOCHO(Metro.SEOUL, "서초구"),
    SEONGDONG(Metro.SEOUL, "성동구"),
    SEONGBUK(Metro.SEOUL, "성북구"),
    SONGPA(Metro.SEOUL, "송파구"),
    YANGCHEON(Metro.SEOUL, "양천구"),
    JUNG(Metro.SEOUL, "중구"),
    JONGNO(Metro.SEOUL, "종로구"),
    YONGSAN(Metro.SEOUL, "용산구"),
    YEONGDEUNGPO(Metro.SEOUL, "영등포구"),
    EUNPYEONG(Metro.SEOUL, "은평구"),
    JUNGNANG(Metro.SEOUL, "중랑구");

    private final Metro metro;
    private final String displayName;

    Region(Metro metro, String displayName) {
        this.metro = metro;
        this.displayName = displayName;
    }

    public enum Metro {
        SEOUL,
        GYEONGGI,
        INCHEON,
        BUSAN
    }
}
