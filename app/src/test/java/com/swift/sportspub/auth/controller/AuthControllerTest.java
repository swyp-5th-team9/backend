package com.swift.sportspub.auth.controller;

import com.swift.sportspub.auth.dto.LoginResponse;
import com.swift.sportspub.auth.service.AuthService;
import com.swift.sportspub.user.entity.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    void loginWithKakao_passesAuthorizationHeaderTokenToService() throws Exception {
        LoginResponse loginResponse = new LoginResponse("jwt-access", "jwt-refresh", UserRole.FAN, false, false);
        when(authService.loginWithKakao(eq("kakao-token"))).thenReturn(loginResponse);

        mockMvc.perform(post("/api/v1/auth/login/kakao")
                        .header(HttpHeaders.AUTHORIZATION, "kakao-token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").value("jwt-access"));

        verify(authService).loginWithKakao("kakao-token");
    }

    @Test
    void loginWithNaver_passesAuthorizationHeaderTokenToService() throws Exception {
        LoginResponse loginResponse = new LoginResponse("jwt-access", "jwt-refresh", UserRole.FAN, true, false);
        when(authService.loginWithNaver(eq("naver-token"))).thenReturn(loginResponse);

        mockMvc.perform(post("/api/v1/auth/login/naver")
                        .header(HttpHeaders.AUTHORIZATION, "naver-token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.onboardingCompleted").value(true));

        verify(authService).loginWithNaver("naver-token");
    }
}
