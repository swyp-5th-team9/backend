package com.swift.sportspub.pub.service;

import com.swift.sportspub.common.exception.BusinessException;
import com.swift.sportspub.common.exception.ErrorCode;
import com.swift.sportspub.pub.dto.PubSummary;
import com.swift.sportspub.pub.entity.Pub;
import com.swift.sportspub.pub.repository.PubImageRepository;
import com.swift.sportspub.pub.repository.PubRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
