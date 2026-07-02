package com.swift.sportspub.auth.jwt;

import com.swift.sportspub.user.repository.UserRepository;
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
    private final UserRepository userRepository;

    /*
     * JWT 인증 흐름:
     * 1. Authorization 헤더에서 Bearer 토큰을 추출한다.
     * 2. JwtProvider로 토큰 서명과 만료 시간을 검증한다.
     * 3. 토큰 subject(sub)에 저장된 userId를 꺼낸다.
     * 4. userId가 활성 회원(deleted_at IS NULL)인지 확인한다.
     * 5. 활성 회원일 때만 userId를 principal로 하는 Authentication을 SecurityContext에 저장한다.
     *
     * 현재 MVP는 OAuth 소셜 로그인만 제공하므로 Username/Password 로그인 구조를 만들지 않는다.
     * UserDetails 대신 userId만 principal로 두되, 탈퇴 회원 AccessToken은 인증 실패(401)로 처리한다. (#60 — existsByUserIdAndDeletedAtIsNull)
     * TODO(후속): Role 기반 인가, @AuthenticationPrincipal 상세 회원 정보가 필요해지면
     * CustomUserDetails와 UserDetailsService로 확장한다.
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
        // #60: 탈퇴 회원 AccessToken은 SecurityContext에 올리지 않아 protected API 접근을 차단한다.
        if (!userRepository.existsByUserIdAndDeletedAtIsNull(userId)) {
            return;
        }

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userId,
                null,
                Collections.emptyList()
        );
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
