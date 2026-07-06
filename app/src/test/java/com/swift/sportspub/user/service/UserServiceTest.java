package com.swift.sportspub.user.service;

import com.swift.sportspub.auth.repository.RefreshTokenRepository;
import com.swift.sportspub.common.exception.BusinessException;
import com.swift.sportspub.common.exception.ErrorCode;
import com.swift.sportspub.team.entity.SportType;
import com.swift.sportspub.team.entity.Team;
import com.swift.sportspub.team.repository.TeamRepository;
import com.swift.sportspub.user.dto.OnboardingRequest;
import com.swift.sportspub.user.dto.UpdateUserRequest;
import com.swift.sportspub.user.entity.OAuthProvider;
import com.swift.sportspub.user.entity.User;
import com.swift.sportspub.user.entity.UserFavoriteTeam;
import com.swift.sportspub.user.repository.UserFavoriteTeamRepository;
import com.swift.sportspub.user.repository.UserRepository;
import com.swift.sportspub.user.repository.WithdrawalReasonRepository;
import com.swift.sportspub.user.storage.UserProfileImageStorageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private static final long USER_ID = 1L;
    private static final String EXISTING_IMAGE_URL = "https://bucket.s3.ap-northeast-2.amazonaws.com/profiles/old.jpg";
    private static final String NEW_IMAGE_URL = "https://bucket.s3.ap-northeast-2.amazonaws.com/profiles/new.jpg";

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserFavoriteTeamRepository userFavoriteTeamRepository;

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private WithdrawalReasonRepository withdrawalReasonRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private UserProfileImageStorageService userProfileImageStorageService;

    @InjectMocks
    private UserService userService;

    @Test
    void onboarding_whenAlreadyCompleted_throwsConflict() {
        User user = activeUser();
        given(userRepository.findActiveById(USER_ID)).willReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.onboarding(USER_ID, new OnboardingRequest("새닉네임", List.of())))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> {
                    BusinessException businessException = (BusinessException) ex;
                    assertThat(businessException.getErrorCode()).isEqualTo(ErrorCode.CONFLICT);
                    assertThat(businessException.getMessage()).isEqualTo("이미 온보딩이 완료된 사용자입니다.");
                });

        verify(userFavoriteTeamRepository, never()).deleteByUserId(USER_ID);
    }

    @Test
    void getMyInfo_whenNoProfileImage_returnsNullProfileImageUrl() {
        User user = activeUser();
        stubActiveUserLookup(user);
        stubEmptyFavoriteTeams();

        var response = userService.getMyInfo(USER_ID);

        assertThat(response.profileImageUrl()).isNull();
        assertThat(response.nickname()).isEqualTo("기존닉네임");
    }

    @Test
    void getMyInfo_whenProfileImageExists_returnsProfileImageUrl() {
        User user = activeUser();
        user.updateProfileImage(EXISTING_IMAGE_URL);
        stubActiveUserLookup(user);
        stubEmptyFavoriteTeams();

        var response = userService.getMyInfo(USER_ID);

        assertThat(response.profileImageUrl()).isEqualTo(EXISTING_IMAGE_URL);
    }

    @Test
    void getMyInfo_returnsFavoriteTeams() {
        User user = activeUser();
        Team team = team(99L, "두산");
        UserFavoriteTeam favoriteTeam = UserFavoriteTeam.builder()
                .user(user)
                .team(team)
                .build();

        stubActiveUserLookup(user);
        given(userFavoriteTeamRepository.findByUserUserIdOrderByCreatedAtAsc(USER_ID))
                .willReturn(List.of(favoriteTeam));

        var response = userService.getMyInfo(USER_ID);

        assertThat(response.favoriteTeams()).hasSize(1);
        assertThat(response.favoriteTeams().getFirst().teamId()).isEqualTo(99L);
        assertThat(response.favoriteTeams().getFirst().teamName()).isEqualTo("두산");
    }

    @Test
    void updateMyInfo_whenNicknameOnly_updatesNicknameAndKeepsProfileImage() {
        User user = activeUser();
        user.updateProfileImage(EXISTING_IMAGE_URL);
        stubActiveUserLookup(user);

        UpdateUserRequest request = new UpdateUserRequest();
        request.setNickname("새닉네임");

        userService.updateMyInfo(USER_ID, request);

        assertThat(user.getNickname()).isEqualTo("새닉네임");
        assertThat(user.getProfileImageUrl()).isEqualTo(EXISTING_IMAGE_URL);
        verify(userProfileImageStorageService, never()).upload(any());
        verify(userFavoriteTeamRepository, never()).deleteByUserId(USER_ID);
    }

    @Test
    void updateMyInfo_whenTeamIdsOnly_replacesFavoriteTeams() {
        User user = activeUser();
        stubActiveUserLookup(user);
        Team team = team(10L, "LG");
        given(teamRepository.findAllById(List.of(10L))).willReturn(List.of(team));

        UpdateUserRequest request = new UpdateUserRequest();
        request.setTeamIds(List.of(10L));

        userService.updateMyInfo(USER_ID, request);

        assertThat(user.getNickname()).isEqualTo("기존닉네임");
        verify(userFavoriteTeamRepository).deleteByUserId(USER_ID);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<UserFavoriteTeam>> captor = ArgumentCaptor.forClass(List.class);
        verify(userFavoriteTeamRepository).saveAll(captor.capture());
        assertThat(captor.getValue()).hasSize(1);
        assertThat(captor.getValue().getFirst().getTeamId()).isEqualTo(10L);
        verify(userProfileImageStorageService, never()).upload(any());
    }

    @Test
    void updateMyInfo_whenProfileImageOnly_uploadsAndStoresUrl() {
        User user = activeUser();
        stubActiveUserLookup(user);
        MockMultipartFile profileImage = jpegFile("profile.jpg", new byte[]{1, 2, 3});
        given(userProfileImageStorageService.upload(profileImage)).willReturn(NEW_IMAGE_URL);

        UpdateUserRequest request = new UpdateUserRequest();
        request.setProfileImage(profileImage);

        userService.updateMyInfo(USER_ID, request);

        assertThat(user.getProfileImageUrl()).isEqualTo(NEW_IMAGE_URL);
        assertThat(user.getNickname()).isEqualTo("기존닉네임");
        verify(userProfileImageStorageService).upload(profileImage);
    }

    @Test
    void updateMyInfo_whenNicknameAndProfileImage_updatesBoth() {
        User user = activeUser();
        stubActiveUserLookup(user);
        MockMultipartFile profileImage = jpegFile("profile.jpg", new byte[]{1, 2, 3});
        given(userProfileImageStorageService.upload(profileImage)).willReturn(NEW_IMAGE_URL);

        UpdateUserRequest request = new UpdateUserRequest();
        request.setNickname("새닉네임");
        request.setProfileImage(profileImage);

        userService.updateMyInfo(USER_ID, request);

        assertThat(user.getNickname()).isEqualTo("새닉네임");
        assertThat(user.getProfileImageUrl()).isEqualTo(NEW_IMAGE_URL);
    }

    @Test
    void updateMyInfo_whenTeamIdsAndProfileImage_updatesBoth() {
        User user = activeUser();
        stubActiveUserLookup(user);
        Team team = team(10L, "LG");
        given(teamRepository.findAllById(List.of(10L))).willReturn(List.of(team));
        MockMultipartFile profileImage = jpegFile("profile.jpg", new byte[]{1, 2, 3});
        given(userProfileImageStorageService.upload(profileImage)).willReturn(NEW_IMAGE_URL);

        UpdateUserRequest request = new UpdateUserRequest();
        request.setTeamIds(List.of(10L));
        request.setProfileImage(profileImage);

        userService.updateMyInfo(USER_ID, request);

        assertThat(user.getProfileImageUrl()).isEqualTo(NEW_IMAGE_URL);
        verify(userFavoriteTeamRepository).deleteByUserId(USER_ID);
    }

    @Test
    void updateMyInfo_whenAllFields_updatesNicknameTeamIdsAndProfileImage() {
        User user = activeUser();
        stubActiveUserLookup(user);
        Team team = team(10L, "LG");
        given(teamRepository.findAllById(List.of(10L))).willReturn(List.of(team));
        MockMultipartFile profileImage = jpegFile("profile.jpg", new byte[]{1, 2, 3});
        given(userProfileImageStorageService.upload(profileImage)).willReturn(NEW_IMAGE_URL);

        UpdateUserRequest request = new UpdateUserRequest();
        request.setNickname("새닉네임");
        request.setTeamIds(List.of(10L));
        request.setProfileImage(profileImage);

        userService.updateMyInfo(USER_ID, request);

        assertThat(user.getNickname()).isEqualTo("새닉네임");
        assertThat(user.getProfileImageUrl()).isEqualTo(NEW_IMAGE_URL);
        verify(userFavoriteTeamRepository).deleteByUserId(USER_ID);
    }

    @Test
    void updateMyInfo_whenUnsupportedExtension_throwsInvalidInput() {
        User user = activeUser();
        stubActiveUserLookup(user);
        MockMultipartFile profileImage = new MockMultipartFile(
                "profileImage", "profile.pdf", "application/pdf", new byte[]{1}
        );

        UpdateUserRequest request = new UpdateUserRequest();
        request.setProfileImage(profileImage);

        assertThatThrownBy(() -> userService.updateMyInfo(USER_ID, request))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> {
                    BusinessException businessException = (BusinessException) ex;
                    assertThat(businessException.getErrorCode()).isEqualTo(ErrorCode.INVALID_INPUT);
                    assertThat(businessException.getMessage()).isEqualTo("지원하지 않는 이미지 형식");
                });

        verify(userProfileImageStorageService, never()).upload(any());
    }

    @Test
    void updateMyInfo_whenUnsupportedContentType_throwsInvalidInput() {
        User user = activeUser();
        stubActiveUserLookup(user);
        MockMultipartFile profileImage = new MockMultipartFile(
                "profileImage", "profile.bin", "application/octet-stream", new byte[]{1}
        );

        UpdateUserRequest request = new UpdateUserRequest();
        request.setProfileImage(profileImage);

        assertThatThrownBy(() -> userService.updateMyInfo(USER_ID, request))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> assertThat(((BusinessException) ex).getMessage())
                        .isEqualTo("지원하지 않는 이미지 형식"));

        verify(userProfileImageStorageService, never()).upload(any());
    }

    @Test
    void updateMyInfo_whenImageSizeExceeded_throwsInvalidInput() {
        User user = activeUser();
        stubActiveUserLookup(user);
        byte[] oversized = new byte[(int) (10L * 1024 * 1024) + 1];
        MockMultipartFile profileImage = jpegFile("profile.jpg", oversized);

        UpdateUserRequest request = new UpdateUserRequest();
        request.setProfileImage(profileImage);

        assertThatThrownBy(() -> userService.updateMyInfo(USER_ID, request))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> assertThat(((BusinessException) ex).getMessage())
                        .isEqualTo("프로필 이미지 용량(10MB) 초과"));

        verify(userProfileImageStorageService, never()).upload(any());
    }

    @Test
    void updateMyInfo_whenEmptyProfileImage_keepsExistingImage() {
        User user = activeUser();
        user.updateProfileImage(EXISTING_IMAGE_URL);
        stubActiveUserLookup(user);

        UpdateUserRequest request = new UpdateUserRequest();
        request.setProfileImage(new MockMultipartFile("profileImage", "", "image/jpeg", new byte[0]));

        userService.updateMyInfo(USER_ID, request);

        assertThat(user.getProfileImageUrl()).isEqualTo(EXISTING_IMAGE_URL);
        verify(userProfileImageStorageService, never()).upload(any());
    }

    @Test
    void updateMyInfo_whenProfileImageNotProvided_keepsExistingImage() {
        User user = activeUser();
        user.updateProfileImage(EXISTING_IMAGE_URL);
        stubActiveUserLookup(user);

        UpdateUserRequest request = new UpdateUserRequest();
        request.setNickname("새닉네임");

        userService.updateMyInfo(USER_ID, request);

        assertThat(user.getProfileImageUrl()).isEqualTo(EXISTING_IMAGE_URL);
        verify(userProfileImageStorageService, never()).upload(any());
    }

    private User activeUser() {
        User activeUser = User.createOAuthUser(OAuthProvider.KAKAO, "oauth-1");
        ReflectionTestUtils.setField(activeUser, "userId", USER_ID);
        activeUser.completeOnboarding("기존닉네임");
        return activeUser;
    }

    private void stubActiveUserLookup(User user) {
        given(userRepository.findActiveById(USER_ID)).willReturn(Optional.of(user));
    }

    private void stubEmptyFavoriteTeams() {
        given(userFavoriteTeamRepository.findByUserUserIdOrderByCreatedAtAsc(USER_ID))
                .willReturn(List.of());
    }

    private Team team(Long teamId, String shortName) {
        Team team = Team.builder()
                .sportType(SportType.KBO)
                .name(shortName + " 팀")
                .shortName(shortName)
                .build();
        ReflectionTestUtils.setField(team, "teamId", teamId);
        return team;
    }

    private MockMultipartFile jpegFile(String filename, byte[] content) {
        return new MockMultipartFile("profileImage", filename, "image/jpeg", content);
    }
}
