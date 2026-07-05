package com.swift.sportspub.user.service;

import com.swift.sportspub.auth.repository.RefreshTokenRepository;
import com.swift.sportspub.common.exception.BusinessException;
import com.swift.sportspub.common.exception.ErrorCode;
import com.swift.sportspub.team.entity.Team;
import com.swift.sportspub.team.repository.TeamRepository;
import com.swift.sportspub.user.dto.FavoriteTeamResponse;
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
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserFavoriteTeamRepository userFavoriteTeamRepository;
    private final TeamRepository teamRepository;
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

        updateNickname(user, request.getNickname());
        replaceFavoriteTeams(user, request.getTeamIds());
    }

    @Transactional
    public void onboarding(Long userId, OnboardingRequest request) {
        User user = getUser(userId);
        // #60: 온보딩은 1회만 허용. 재호출 시 프로필·선호 구단이 덮어쓰이는 것을 방지한다.
        if (user.isOnboardingCompleted()) {
            throw new BusinessException(ErrorCode.CONFLICT, "이미 온보딩이 완료된 사용자입니다.");
        }
        user.completeOnboarding(request.nickname());
        replaceFavoriteTeams(user, request.teamIds());
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
        Map<Long, Team> teamById = validateTeamIdsExist(distinctTeamIds);

        List<UserFavoriteTeam> favoriteTeams = distinctTeamIds.stream()
                .map(teamId -> UserFavoriteTeam.builder()
                        .user(user)
                        .team(teamById.get(teamId))
                        .build())
                .toList();

        userFavoriteTeamRepository.deleteByUserId(user.getUserId());
        userFavoriteTeamRepository.saveAll(favoriteTeams);
    }

    private Map<Long, Team> validateTeamIdsExist(List<Long> teamIds) {
        if (teamIds.isEmpty()) {
            return Map.of();
        }
        Map<Long, Team> teamById = teamRepository.findAllById(teamIds).stream()
                .collect(Collectors.toMap(Team::getTeamId, Function.identity()));
        if (teamById.size() != teamIds.size()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "존재하지 않는 teamId가 포함되어 있습니다.");
        }
        return teamById;
    }

    private UserResponse toUserResponse(User user) {
        List<UserFavoriteTeam> favoriteTeams =
                userFavoriteTeamRepository.findByUserUserIdOrderByCreatedAtAsc(user.getUserId());

        List<FavoriteTeamResponse> favoriteTeamResponses = buildFavoriteTeamResponses(favoriteTeams);

        return new UserResponse(
                user.getUserId(),
                user.getNickname(),
                user.getProfileImageUrl(),
                user.getRole(),
                user.isOnboardingCompleted(),
                favoriteTeamResponses
        );
    }

    private List<FavoriteTeamResponse> buildFavoriteTeamResponses(List<UserFavoriteTeam> favoriteTeams) {
        if (favoriteTeams.isEmpty()) {
            return List.of();
        }

        return favoriteTeams.stream()
                .map(favoriteTeam -> new FavoriteTeamResponse(
                        favoriteTeam.getTeamId(),
                        favoriteTeam.getTeam().getShortName()
                ))
                .toList();
    }
}
