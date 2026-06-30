package com.swift.sportspub.pub.entity;

import lombok.Getter;

@Getter
public enum PubFoodCode {

    CHICKEN("치킨"),
    PIZZA("피자"),
    TACO("타코"),
    FRY("튀김"),
    STEW("찌개/탕"),
    GRILLED("구이/볶음"),
    BUNSIK("분식"),
    DRY_SNACK("마른안주"),
    SOJU("소주"),
    BEER("맥주"),
    COCKTAIL("칵테일"),
    HIGHBALL("하이볼");

    private final String displayName;

    PubFoodCode(String displayName) {
        this.displayName = displayName;
    }
}
