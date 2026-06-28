package com.swift.sportspub.pub.repository;

import com.swift.sportspub.pub.dto.PubListSearchCondition;

import java.util.List;

public interface PubRepositoryCustom {

    PubIdPage searchPubIds(PubListSearchCondition condition);

    record PubIdPage(List<Long> pubIds, long totalElements) {
    }
}
