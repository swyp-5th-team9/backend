package com.swift.sportspub.pub.service;

import com.swift.sportspub.common.exception.BusinessException;
import com.swift.sportspub.common.exception.ErrorCode;
import com.swift.sportspub.pub.dto.PubSummary;
import com.swift.sportspub.pub.entity.Pub;
import com.swift.sportspub.pub.entity.PubImage;
import com.swift.sportspub.pub.repository.PubImageRepository;
import com.swift.sportspub.pub.repository.PubRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PubQueryService {

    private final PubRepository pubRepository;
    private final PubImageRepository pubImageRepository;

    @Transactional(readOnly = true)
    public PubSummary findSummary(Long pubId) {
        Pub pub = pubRepository.findById(pubId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));

        String thumbnailUrl = pubImageRepository
                .findFirstByPubIdOrderByDisplayOrderAsc(pubId)
                .map(image -> image.getImageUrl())
                .orElse(null);

        return PubSummary.of(pub, thumbnailUrl);
    }

    @Transactional(readOnly = true)
    public List<PubSummary> findSummariesByIds(List<Long> pubIds) {
        if (pubIds == null || pubIds.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long, Pub> pubById = new HashMap<>();
        for (Pub pub : pubRepository.findAllById(pubIds)) {
            pubById.put(pub.getPubId(), pub);
        }

        Map<Long, String> thumbnailByPubId = new HashMap<>();
        for (PubImage image : pubImageRepository.findAllByPubIdInOrderByPubIdAscDisplayOrderAsc(pubIds)) {
            thumbnailByPubId.putIfAbsent(image.getPubId(), image.getImageUrl());
        }

        List<PubSummary> summaries = new ArrayList<>(pubIds.size());
        for (Long pubId : pubIds) {
            Pub pub = pubById.get(pubId);
            if (pub == null) {
                continue;
            }
            summaries.add(PubSummary.of(pub, thumbnailByPubId.get(pubId)));
        }
        return summaries;
    }
}
