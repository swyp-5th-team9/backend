package com.swift.sportspub.favorite.service;

import com.swift.sportspub.common.exception.BusinessException;
import com.swift.sportspub.common.exception.ErrorCode;
import com.swift.sportspub.favorite.entity.Favorite;
import com.swift.sportspub.favorite.repository.FavoriteRepository;
import com.swift.sportspub.user.entity.User;
import com.swift.sportspub.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private static final int MAX_FAVORITES_PER_USER = 30;

    private final UserService userService;
    private final FavoriteRepository favoriteRepository;

    @Transactional
    public void addFavorite(Long userId, Long pubId) {
        User user = userService.getUser(userId);

        validatePubExists(pubId);
        validateFavoriteLimit(userId);
        validateNotDuplicate(userId, pubId);

        favoriteRepository.save(
                Favorite.builder()
                        .user(user)
                        .pubId(pubId)
                        .build()
        );
    }

    private void validatePubExists(Long pubId) {
        if (!favoriteRepository.existsActivePub(pubId)) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "존재하지 않는 pubId입니다.");
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
}
