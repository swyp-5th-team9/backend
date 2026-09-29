package com.swift.sportspub.pub.entity;

import lombok.Getter;

@Getter
public enum PubThemeCode {

    EXOTIC("이국적"),
    SPACIOUS_VIEW("넓은 뷰"),
    SPECIAL_MENU("특별 메뉴"),
    FRESH("깔끔/신선"),
    COMFY_SEAT("편안한 좌석");

    private final String displayName;

    PubThemeCode(String displayName) {
        this.displayName = displayName;
    }
}
