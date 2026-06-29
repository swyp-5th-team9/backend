package com.swift.sportspub.pub.dto;

import java.util.List;

public enum BusinessDayFilter {
    EVERYDAY(List.of(1, 2, 3, 4, 5, 6, 7)),
    WEEKDAY(List.of(1, 2, 3, 4, 5)),
    WEEKEND(List.of(6, 7)),
    MON(List.of(1)),
    TUE(List.of(2)),
    WED(List.of(3)),
    THU(List.of(4)),
    FRI(List.of(5)),
    SAT(List.of(6)),
    SUN(List.of(7));

    private final List<Integer> days;

    BusinessDayFilter(List<Integer> days) {
        this.days = days;
    }

    public List<Integer> getDays() {
        return days;
    }
}
