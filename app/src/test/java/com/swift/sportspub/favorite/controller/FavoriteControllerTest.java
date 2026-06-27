package com.swift.sportspub.favorite.controller;

import com.swift.sportspub.common.exception.BusinessException;
import com.swift.sportspub.common.exception.ErrorCode;
import com.swift.sportspub.common.exception.GlobalExceptionHandler;
import com.swift.sportspub.favorite.service.FavoriteService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.doThrow;
import static org.mockito.BDDMockito.verify;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class FavoriteControllerTest {

    @Mock
    private FavoriteService favoriteService;

    @InjectMocks
    private FavoriteController favoriteController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(favoriteController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
                .build();
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(1L, null, Collections.emptyList())
        );
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void addFavorite_returnsSuccess() throws Exception {
        doNothing().when(favoriteService).addFavorite(eq(1L), eq(10L));

        mockMvc.perform(post("/api/v1/favorites/{pubId}", 10L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(favoriteService).addFavorite(1L, 10L);
    }

    @Test
    void addFavorite_duplicate_returnsConflict() throws Exception {
        doThrow(new BusinessException(ErrorCode.CONFLICT, "이미 즐겨찾기한 pub입니다."))
                .when(favoriteService).addFavorite(eq(1L), eq(10L));

        mockMvc.perform(post("/api/v1/favorites/{pubId}", 10L))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("이미 즐겨찾기한 pub입니다."));
    }

    @Test
    void addFavorite_invalidPub_returnsBadRequest() throws Exception {
        doThrow(new BusinessException(ErrorCode.INVALID_INPUT, "존재하지 않는 pubId입니다."))
                .when(favoriteService).addFavorite(eq(1L), eq(99L));

        mockMvc.perform(post("/api/v1/favorites/{pubId}", 99L))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }
}
