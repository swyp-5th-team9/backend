package com.swift.sportspub.pub.dto;

public record PubListSearchCondition(
        String keyword,
        PubSearchFilter filter,
        int page,
        int size
) {
}
