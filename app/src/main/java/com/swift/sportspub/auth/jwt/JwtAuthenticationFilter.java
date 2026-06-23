package com.swift.sportspub.auth.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtProvider jwtProvider;

    /*
     * JWT 인증 흐름:
     * 1. Authorization 헤더에서 Bearer 토큰을 추출한다.
     * 2. JwtProvider로 토큰 서명과 만료 시간을 검증한다.
     * 3. 토큰 subject(sub)에 저장된 userId를 꺼낸다.
     * 4. userId를 principal로 하는 Authentication을 SecurityContext에 저장한다.
     *
     * 현재 MVP는 OAuth 소셜 로그인만 제공하므로 Username/Password 로그인 구조를 만들지 않는다.
     * 또한 권한 기반 인가와 상세 사용자 정보가 아직 필요하지 않아 UserDetails 대신 userId만 principal로 둔다.
     * 추후 Role 인가, 계정 상태 검증, @AuthenticationPrincipal에서 상세 회원 정보가 필요해지면
     * CustomUserDetails와 UserDetailsService로 확장할 수 있다.
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String token = resolveToken(request);

        if (token != null && jwtProvider.validateToken(token) && jwtProvider.isAccessToken(token)) {
            authenticate(request, token);
        }

        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER_PREFIX)) {
            return null;
        }

        return authorizationHeader.substring(BEARER_PREFIX.length());
    }

    private void authenticate(HttpServletRequest request, String token) {
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            return;
        }

        Long userId = jwtProvider.extractUserId(token);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userId,
                null,
                Collections.emptyList()
        );
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
