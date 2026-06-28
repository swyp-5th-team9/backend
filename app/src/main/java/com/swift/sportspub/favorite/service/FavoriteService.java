package com.swift.sportspub.favorite.service;

import com.swift.sportspub.common.exception.BusinessException;
import com.swift.sportspub.common.exception.ErrorCode;
import com.swift.sportspub.favorite.dto.FavoriteDeleteRequest;
import com.swift.sportspub.favorite.dto.FavoriteItemResponse;
import com.swift.sportspub.favorite.dto.FavoriteListResponse;
import com.swift.sportspub.favorite.entity.Favorite;
import com.swift.sportspub.favorite.repository.FavoriteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;

    @Transactional(readOnly = true)
    public FavoriteListResponse getMyFavorites(Long userId) {
        List<FavoriteItemResponse> favorites = favoriteRepository
                .findTop30ByUserUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toFavoriteItemResponse)
                .toList();

        return FavoriteListResponse.of(favorites);
    }

    @Transactional
    public void deleteFavorites(Long userId, FavoriteDeleteRequest request) {
        List<Long> distinctFavoriteIds = toDistinctFavoriteIds(request.favoriteIds());

        List<Favorite> ownedFavorites = favoriteRepository
                .findByFavoriteIdInAndUserUserId(distinctFavoriteIds, userId);

        if (ownedFavorites.size() != distinctFavoriteIds.size()) {
            throw new BusinessException(
                    ErrorCode.NOT_FOUND,
                    "존재하지 않거나 삭제할 수 없는 즐겨찾기가 포함되어 있습니다."
            );
        }

        favoriteRepository.deleteAllInBatch(ownedFavorites);
    }

    private List<Long> toDistinctFavoriteIds(List<Long> favoriteIds) {
        if (favoriteIds.stream().anyMatch(Objects::isNull)) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "favoriteIds에는 null을 포함할 수 없습니다.");
        }

        return new ArrayList<>(new LinkedHashSet<>(favoriteIds));
    }

    private FavoriteItemResponse toFavoriteItemResponse(Favorite favorite) {
        return new FavoriteItemResponse(
                favorite.getFavoriteId(),
                favorite.getPubId(),
                null,
                null
        );
    }
}
