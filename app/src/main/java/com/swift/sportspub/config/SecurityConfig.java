package com.swift.sportspub.config;

import com.swift.sportspub.auth.jwt.JwtAccessDeniedHandler;
import com.swift.sportspub.auth.jwt.JwtAuthenticationEntryPoint;
import com.swift.sportspub.auth.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(withDefaults())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(jwtAccessDeniedHandler)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",
                                "/health",
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/oauth2/**",
                                "/login/**",
                                "/api/v1/auth/login/**",
                                "/api/v1/auth/refresh-token"
                        ).permitAll()
                        /*
                         * 로그인/재발급 API는 아직 유효한 AccessToken이 없는 상황에서도 호출해야 하므로 permitAll로 둔다.
                         * 로그아웃을 포함한 그 외 API는 발급받은 JWT를 Authorization: Bearer 헤더로 보내야 접근할 수 있다.
                         */
                        .anyRequest().authenticated()
                )
                /*
                 * JWT 기반 인증은 세션을 사용하지 않으므로 매 요청마다 필터에서 토큰을 검증한다.
                 * Username/Password 로그인은 사용하지 않지만, Spring Security 인증 체인에서
                 * JWT 인증을 먼저 처리하기 위해 UsernamePasswordAuthenticationFilter 앞에 등록한다.
                 */
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
