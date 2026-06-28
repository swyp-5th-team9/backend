package com.swift.sportspub.pub.entity;

import lombok.Getter;

@Getter
public enum MenuCategory {

    FOOD("음식"),
    DRINK("음료"),
    SET("세트");

    private final String displayName;

    MenuCategory(String displayName) {
        this.displayName = displayName;
    }
}
