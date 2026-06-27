package com.swift.sportspub.pub.entity;

import lombok.Getter;

@Getter
public enum PubFoodCode {

    CHICKEN("치킨"),
    PIZZA("피자"),
    TACO("타코"),
    FRIES("감자튀김"),
    BUNSIK("분식"),
    GRILLED("구이류"),
    DRY_SNACK("마른안주"),
    BEER("맥주"),
    COCKTAIL("칵테일"),
    HIGHBALL("하이볼"),
    SOJU("소주");

    private final String displayName;

    PubFoodCode(String displayName) {
        this.displayName = displayName;
    }
}
