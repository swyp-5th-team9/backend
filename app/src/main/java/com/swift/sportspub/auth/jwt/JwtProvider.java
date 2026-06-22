package com.swift.sportspub.auth.jwt;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtProvider {

    private static final String TOKEN_TYPE_CLAIM = "type";
    private static final String ACCESS_TOKEN_TYPE = "ACCESS";
    private static final String REFRESH_TOKEN_TYPE = "REFRESH";

    private final SecretKey secretKey;
    private final long accessTtlSeconds;
    private final long refreshTtlSeconds;

    public JwtProvider(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.access-ttl-seconds}") long accessTtlSeconds,
            @Value("${app.jwt.refresh-ttl-seconds}") long refreshTtlSeconds
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTtlSeconds = accessTtlSeconds;
        this.refreshTtlSeconds = refreshTtlSeconds;
    }

    /*
     * AccessToken과 RefreshToken은 수명과 사용 목적이 다르기 때문에 분리한다.
     *
     * AccessToken은 API 요청 인증에 자주 사용되므로 짧게 유지해 탈취 피해를 줄이고,
     * RefreshToken은 AccessToken 재발급에만 사용해 사용 빈도와 노출 범위를 낮춘다.
     * 이후 RefreshToken 저장소를 도입하면 재발급, 로그아웃, 토큰 회전 정책을 독립적으로 확장할 수 있다.
     *
     * type claim을 함께 저장해 AccessToken과 RefreshToken을 구조적으로 구분한다.
     * 이렇게 하면 재발급 API에서 AccessToken을 RefreshToken처럼 사용하는 실수를 막을 수 있다.
     */
    public String createAccessToken(Long userId) {
        return createToken(userId, accessTtlSeconds, ACCESS_TOKEN_TYPE);
    }

    public String createRefreshToken(Long userId) {
        return createToken(userId, refreshTtlSeconds, REFRESH_TOKEN_TYPE);
    }

    /*
     * JWT subject(sub)는 토큰의 주체를 나타내는 표준 클레임이다.
     *
     * 이 프로젝트에서는 인증 주체가 User이므로 userId를 sub에 저장한다.
     * JwtAuthenticationFilter는 sub에서 userId를 꺼내 User 조회 또는 Authentication 생성을 수행할 수 있다.
     */
    public Long extractUserId(String token) {
        String subject = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();

        return Long.valueOf(subject);
    }

    public String extractTokenType(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get(TOKEN_TYPE_CLAIM, String.class);
    }

    public boolean isRefreshToken(String token) {
        return REFRESH_TOKEN_TYPE.equals(extractTokenType(token));
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private String createToken(Long userId, long ttlSeconds, String tokenType) {
        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(ttlSeconds);

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim(TOKEN_TYPE_CLAIM, tokenType)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiresAt))
                .signWith(secretKey)
                .compact();
    }
}
