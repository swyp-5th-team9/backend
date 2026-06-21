package com.swift.sportspub.config;

import com.swift.sportspub.auth.jwt.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(withDefaults())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",
                                "/health",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/oauth2/**",
                                "/login/**",
                                "/api/v1/auth/**"
                        ).permitAll()
                        /*
                         * 로그인 API는 아직 JWT가 없는 사용자가 호출해야 하므로 permitAll로 둔다.
                         * 그 외 API는 발급받은 JWT를 Authorization: Bearer 헤더로 보내야 접근할 수 있다.
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
