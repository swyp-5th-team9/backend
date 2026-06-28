package com.swift.sportspub.pub.dto;

import com.swift.sportspub.pub.entity.PubBusinessHours;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public enum BusinessStatus {
    OPEN_NOW,
    CLOSING_SOON,
    CLOSED_NOW;

    private static final long CLOSING_SOON_THRESHOLD_MINUTES = 60;

    public static BusinessStatus calculate(List<PubBusinessHours> hoursOfPub, LocalDateTime now) {
        if (hoursOfPub == null || hoursOfPub.isEmpty()) {
            return CLOSED_NOW;
        }

        short todayDow = (short) now.getDayOfWeek().getValue();
        LocalTime nowTime = now.toLocalTime();

        for (PubBusinessHours h : hoursOfPub) {
            if (h.getDayOfWeek() != todayDow) {
                continue;
            }
            if (Boolean.TRUE.equals(h.getIsClosed())) {
                return CLOSED_NOW;
            }
            LocalTime open = h.getOpenTime();
            LocalTime close = h.getCloseTime();
            if (open == null || close == null) {
                return CLOSED_NOW;
            }

            boolean overnight = !close.isAfter(open);
            boolean isOpen = overnight
                    ? (!nowTime.isBefore(open) || nowTime.isBefore(close))
                    : (!nowTime.isBefore(open) && nowTime.isBefore(close));

            if (!isOpen) {
                return CLOSED_NOW;
            }

            // 자정 넘김 영업 중 저녁 시간대(now >= open)일 때 close 는 다음날
            LocalDateTime closeAt = (overnight && !nowTime.isBefore(open))
                    ? now.toLocalDate().plusDays(1).atTime(close)
                    : now.toLocalDate().atTime(close);
            long minutesToClose = Duration.between(now, closeAt).toMinutes();
            return minutesToClose <= CLOSING_SOON_THRESHOLD_MINUTES ? CLOSING_SOON : OPEN_NOW;
        }
        return CLOSED_NOW;
    }
}
