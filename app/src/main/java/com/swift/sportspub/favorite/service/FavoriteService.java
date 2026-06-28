package com.swift.sportspub.favorite.service;

import com.swift.sportspub.favorite.dto.FavoriteItemResponse;
import com.swift.sportspub.favorite.dto.FavoriteListResponse;
import com.swift.sportspub.favorite.entity.Favorite;
import com.swift.sportspub.favorite.repository.FavoriteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    private FavoriteItemResponse toFavoriteItemResponse(Favorite favorite) {
        return new FavoriteItemResponse(
                favorite.getFavoriteId(),
                favorite.getPubId(),
                null,
                null
        );
    }
}
