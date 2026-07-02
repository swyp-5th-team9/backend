package com.swift.sportspub.favorite.service;

import com.swift.sportspub.common.exception.BusinessException;
import com.swift.sportspub.common.exception.ErrorCode;
import com.swift.sportspub.favorite.dto.FavoriteDeleteRequest;
import com.swift.sportspub.favorite.dto.FavoriteItemResponse;
import com.swift.sportspub.favorite.dto.FavoriteListResponse;
import com.swift.sportspub.favorite.entity.Favorite;
import com.swift.sportspub.favorite.repository.FavoriteRepository;
import com.swift.sportspub.pub.entity.Pub;
import com.swift.sportspub.pub.entity.PubImage;
import com.swift.sportspub.pub.repository.PubImageRepository;
import com.swift.sportspub.pub.repository.PubRepository;
import com.swift.sportspub.user.entity.User;
import com.swift.sportspub.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private static final int MAX_FAVORITES_PER_USER = 30;

    private final UserService userService;
    private final FavoriteRepository favoriteRepository;
    private final PubRepository pubRepository;
    private final PubImageRepository pubImageRepository;

    @Transactional(readOnly = true)
    public FavoriteListResponse getMyFavorites(Long userId) {
        List<Favorite> favorites = favoriteRepository.findTop30ByUserUserIdOrderByCreatedAtDesc(userId);
        if (favorites.isEmpty()) {
            return FavoriteListResponse.of(List.of());
        }

        List<Long> pubIds = favorites.stream()
                .map(Favorite::getPubId)
                .toList();

        Map<Long, Pub> pubById = pubRepository.findAllById(pubIds).stream()
                .collect(Collectors.toMap(Pub::getPubId, Function.identity()));

        Map<Long, String> thumbnailByPubId = buildThumbnailByPubId(pubIds);

        List<FavoriteItemResponse> items = favorites.stream()
                .map(favorite -> toFavoriteItemResponse(
                        favorite,
                        pubById.get(favorite.getPubId()),
                        thumbnailByPubId.get(favorite.getPubId())
                ))
                .toList();

        return FavoriteListResponse.of(items);
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

    @Transactional
    public Long addFavorite(Long userId, Long pubId) {
        User user = userService.getUser(userId);

        validatePubExists(pubId);
        validateNotDuplicate(userId, pubId);
        validateFavoriteLimit(userId);

        Favorite saved = favoriteRepository.save(
                Favorite.builder()
                        .user(user)
                        .pubId(pubId)
                        .build()
        );
        return saved.getFavoriteId();
    }

    private void validatePubExists(Long pubId) {
        if (!favoriteRepository.existsActivePub(pubId)) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "존재하지 않는 pubId입니다.");
        }
    }

    private void validateFavoriteLimit(Long userId) {
        if (favoriteRepository.countByUserUserId(userId) >= MAX_FAVORITES_PER_USER) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "즐겨찾기는 최대 30개까지 등록할 수 있습니다.");
        }
    }

    private void validateNotDuplicate(Long userId, Long pubId) {
        if (favoriteRepository.existsByUserUserIdAndPubId(userId, pubId)) {
            throw new BusinessException(ErrorCode.CONFLICT, "이미 즐겨찾기한 pub입니다.");
        }
    }

    private Map<Long, String> buildThumbnailByPubId(List<Long> pubIds) {
        Map<Long, String> thumbnailByPubId = new HashMap<>();
        for (PubImage image : pubImageRepository.findAllByPubIdInOrderByPubIdAscDisplayOrderAsc(pubIds)) {
            thumbnailByPubId.putIfAbsent(image.getPubId(), image.getImageUrl());
        }
        return thumbnailByPubId;
    }

    private FavoriteItemResponse toFavoriteItemResponse(Favorite favorite, Pub pub, String thumbnailImageUrl) {
        if (pub == null) {
            return new FavoriteItemResponse(
                    favorite.getFavoriteId(),
                    favorite.getPubId(),
                    null,
                    null,
                    null
            );
        }

        return new FavoriteItemResponse(
                favorite.getFavoriteId(),
                favorite.getPubId(),
                pub.getName(),
                resolveRegionDisplay(pub),
                thumbnailImageUrl
        );
    }

    private String resolveRegionDisplay(Pub pub) {
        if (pub.getSubRegion() != null) {
            return pub.getSubRegion().getDisplayName();
        }
        return pub.getRegion().getDisplayName();
    }
}
