package com.swift.sportspub.user.service;

import com.swift.sportspub.auth.repository.RefreshTokenRepository;
import com.swift.sportspub.common.exception.BusinessException;
import com.swift.sportspub.common.exception.ErrorCode;
import com.swift.sportspub.user.dto.OnboardingRequest;
import com.swift.sportspub.user.dto.UpdateUserRequest;
import com.swift.sportspub.user.dto.UserResponse;
import com.swift.sportspub.user.dto.WithdrawRequest;
import com.swift.sportspub.user.entity.User;
import com.swift.sportspub.user.entity.UserFavoriteTeam;
import com.swift.sportspub.user.entity.WithdrawalReason;
import com.swift.sportspub.user.exception.UserNotFoundException;
import com.swift.sportspub.user.repository.UserFavoriteTeamRepository;
import com.swift.sportspub.user.repository.UserRepository;
import com.swift.sportspub.user.repository.WithdrawalReasonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserFavoriteTeamRepository userFavoriteTeamRepository;
    private final WithdrawalReasonRepository withdrawalReasonRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    /*
     * 현재 JWT 인증 구조는 SecurityContext principal에 Long userId를 저장한다.
     * 인증된 API는 탈퇴하지 않은 활성 회원만 조회한다.
     */
    @Transactional(readOnly = true)
    public User getUser(Long userId) {
        return userRepository.findActiveById(userId)
                .orElseThrow(UserNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public UserResponse getMyInfo(Long userId) {
        User user = getUser(userId);
        return toUserResponse(user);
    }

    @Transactional
    public void updateMyInfo(Long userId, UpdateUserRequest request) {
        User user = getUser(userId);

        updateNickname(user, request.nickname());
        replaceFavoriteTeams(user, request.teamIds());
    }

    /*
     * 현재 온보딩에서는 teamIds를 저장하지 않고 최대 개수 검증만 수행한다.
     * 선호 구단 저장 및 수정은 PATCH /api/v1/users/me 에서 수행한다.
     * TODO: Team 도메인 구현 후 온보딩 저장 정책 재검토
     */
    @Transactional
    public void onboarding(Long userId, OnboardingRequest request) {
        User user = getUser(userId);
        user.completeOnboarding(request.nickname());
    }

    /*
     * 회원 탈퇴는 Soft Delete로 처리한다.
     * 탈퇴 사유는 append-only 이력으로 저장하고, RefreshToken은 즉시 폐기한다.
     * MVP에서는 동일 OAuth 재로그인 시 AuthService가 계정을 복구한다.
     */
    @Transactional
    public void withdraw(Long userId, WithdrawRequest request) {
        User user = userRepository.findActiveById(userId)
                .orElseThrow(UserNotFoundException::new);

        withdrawalReasonRepository.save(
                WithdrawalReason.create(user, request.reasonCode(), request.detail())
        );
        user.softDelete();
        refreshTokenRepository.deleteByUserUserId(userId);
    }

    private void updateNickname(User user, String nickname) {
        if (nickname == null) {
            return;
        }
        if (nickname.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "nickname은 공백일 수 없습니다.");
        }

        user.updateProfile(nickname);
    }

    private void replaceFavoriteTeams(User user, List<Long> teamIds) {
        if (teamIds == null) {
            return;
        }
        if (teamIds.stream().anyMatch(Objects::isNull)) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "teamIds에는 null을 포함할 수 없습니다.");
        }

        List<Long> distinctTeamIds = new ArrayList<>(new LinkedHashSet<>(teamIds));
        /*
         * 현재는 Team 도메인이 없어 teamId 값만 저장한다.
         * TODO: Team 도메인 구현 후 teamIds 존재 여부 검증 추가
         * 검증은 기존 선호 구단 삭제 전에 수행해야 한다.
         */
        List<UserFavoriteTeam> favoriteTeams = distinctTeamIds.stream()
                .map(teamId -> UserFavoriteTeam.builder()
                        .user(user)
                        .teamId(teamId)
                        .build())
                .toList();

        userFavoriteTeamRepository.deleteByUserId(user.getUserId());
        userFavoriteTeamRepository.saveAll(favoriteTeams);
    }

    private UserResponse toUserResponse(User user) {
        /*
         * TODO:
         * Team 도메인 구현 후 UserFavoriteTeam 조회 추가
         * favoriteTeams 응답에 teamId 및 teamName 포함
         * 현재는 Team 정보 조회 기능이 없어 빈 배열 반환
         */
        return new UserResponse(
                user.getUserId(),
                user.getNickname(),
                user.getRole(),
                user.isOnboardingCompleted(),
                List.of()
        );
    }
}
