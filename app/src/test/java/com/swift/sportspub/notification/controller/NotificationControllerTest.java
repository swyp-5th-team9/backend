package com.swift.sportspub.notification.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.swift.sportspub.common.exception.GlobalExceptionHandler;
import com.swift.sportspub.notification.dto.NotificationResponse;
import com.swift.sportspub.notification.entity.DeepLinkType;
import com.swift.sportspub.notification.entity.NotificationType;
import com.swift.sportspub.notification.service.NotificationService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private NotificationController notificationController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        mockMvc = MockMvcBuilders.standaloneSetup(notificationController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
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
    void getMyNotifications_returnsList() throws Exception {
        NotificationResponse item = new NotificationResponse(
                1L,
                120L,
                List.of(1L, 2L),
                NotificationType.GAME_REMINDER,
                "오늘 경기 있어요!",
                "LG 트윈스와 두산 베어스 경기가 오늘 오후 6시에 진행돼요.",
                OffsetDateTime.parse("2026-08-24T11:30:00+09:00"),
                false,
                DeepLinkType.TODAY_PUBS
        );
        given(notificationService.getMyNotifications(1L)).willReturn(List.of(item));

        mockMvc.perform(get("/api/v1/notifications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].notificationId").value(1))
                .andExpect(jsonPath("$.data[0].matchId").value(120))
                .andExpect(jsonPath("$.data[0].teamIds[0]").value(1))
                .andExpect(jsonPath("$.data[0].teamIds[1]").value(2))
                .andExpect(jsonPath("$.data[0].isRead").value(false))
                .andExpect(jsonPath("$.data[0].deepLinkType").value("TODAY_PUBS"))
                .andExpect(jsonPath("$.data[0].createdAt").value("2026-08-24T11:30:00+09:00"));

        verify(notificationService).getMyNotifications(1L);
    }

    @Test
    void getMyNotifications_whenEmpty_returnsEmptyArray() throws Exception {
        given(notificationService.getMyNotifications(1L)).willReturn(List.of());

        mockMvc.perform(get("/api/v1/notifications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty());
    }
}
