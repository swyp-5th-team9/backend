package com.swift.sportspub.crawler.service;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class StadiumNameResolver {

    private static final Map<String, String> SHORT_TO_FULL = Map.of(
            "잠실", "서울 잠실야구장",
            "수원", "수원 KT위즈파크",
            "문학", "인천 SSG랜더스필드",
            "사직", "부산 사직야구장",
            "창원", "창원 NC파크",
            "대전", "대전 한화생명 볼파크",
            "대구", "대구 삼성라이온즈파크",
            "광주", "광주-기아 챔피언스 필드",
            "고척", "고척 스카이돔"
    );

    public String resolve(String shortName) {
        if (shortName == null || shortName.isBlank()) return null;
        return SHORT_TO_FULL.getOrDefault(shortName, shortName);
    }
}
