package com.swift.sportspub.pub.repository;

import com.swift.sportspub.pub.dto.PubListSearchCondition;
import com.swift.sportspub.pub.dto.PubMapSearchCondition;

import java.util.List;

public interface PubRepositoryCustom {

    PubIdPage searchPubIds(PubListSearchCondition condition);

    List<Long> searchMapPubIds(PubMapSearchCondition condition);

    record PubIdPage(List<Long> pubIds, long totalElements) {
    }
}
