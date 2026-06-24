package com.swift.sportspub.user.service;

import com.swift.sportspub.common.exception.BusinessException;
import com.swift.sportspub.common.exception.ErrorCode;
import com.swift.sportspub.user.dto.OnboardingRequest;
import com.swift.sportspub.user.dto.UpdateUserRequest;
import com.swift.sportspub.user.dto.UserResponse;
import com.swift.sportspub.user.entity.User;
import com.swift.sportspub.user.entity.UserFavoriteTeam;
import com.swift.sportspub.user.exception.UserNotFoundException;
import com.swift.sportspub.user.repository.UserFavoriteTeamRepository;
import com.swift.sportspub.user.repository.UserRepository;
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

    /*
     * 현재 JWT 인증 구조는 SecurityContext principal에 Long userId를 저장한다.
     * 후속 User API(#11~#13)는 이 메서드를 통해 인증된 사용자를 일관되게 조회한다.
     */
    @Transactional(readOnly = true)
    public User getUser(Long userId) {
        return userRepository.findById(userId)
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
     * teamIds는 현재 최대 개수 검증만 수행한다.
     * 선호 구단 저장은 #12에서 Team/UserFavoriteTeam 구조를 만들 때 함께 연결한다.
     */
    @Transactional
    public void onboarding(Long userId, OnboardingRequest request) {
        User user = getUser(userId);
        user.completeOnboarding(request.nickname());
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
        List<UserFavoriteTeam> favoriteTeams = distinctTeamIds.stream()
                .map(teamId -> UserFavoriteTeam.builder()
                        .user(user)
                        .teamId(teamId)
                        .build())
                .toList();

        // TODO: Team 도메인 구현 후 teamIds 존재 여부 검증 추가
        // 검증은 기존 선호 구단 삭제 전에 수행해야 한다.
        userFavoriteTeamRepository.deleteByUserId(user.getUserId());
        userFavoriteTeamRepository.saveAll(favoriteTeams);
    }

    private UserResponse toUserResponse(User user) {
        return new UserResponse(
                user.getUserId(),
                user.getNickname(),
                user.getRole(),
                user.isOnboardingCompleted(),
                List.of()
        );
    }
}
