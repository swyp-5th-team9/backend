package com.swift.sportspub.auth.jwt;

import com.swift.sportspub.user.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    private static final String ACCESS_TOKEN = "valid-access-token";

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private UserRepository userRepository;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_whenActiveUser_setsAuthentication() throws Exception {
        given(request.getHeader(HttpHeaders.AUTHORIZATION)).willReturn("Bearer " + ACCESS_TOKEN);
        given(jwtProvider.validateToken(ACCESS_TOKEN)).willReturn(true);
        given(jwtProvider.isAccessToken(ACCESS_TOKEN)).willReturn(true);
        given(jwtProvider.extractUserId(ACCESS_TOKEN)).willReturn(1L);
        given(userRepository.existsByUserIdAndDeletedAtIsNull(1L)).willReturn(true);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).isEqualTo(1L);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_whenWithdrawnUser_doesNotSetAuthentication() throws Exception {
        given(request.getHeader(HttpHeaders.AUTHORIZATION)).willReturn("Bearer " + ACCESS_TOKEN);
        given(jwtProvider.validateToken(ACCESS_TOKEN)).willReturn(true);
        given(jwtProvider.isAccessToken(ACCESS_TOKEN)).willReturn(true);
        given(jwtProvider.extractUserId(ACCESS_TOKEN)).willReturn(1L);
        given(userRepository.existsByUserIdAndDeletedAtIsNull(1L)).willReturn(false);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
    }
}
