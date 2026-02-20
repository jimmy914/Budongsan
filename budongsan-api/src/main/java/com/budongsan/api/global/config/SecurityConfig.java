package com.budongsan.api.global.config;

import com.budongsan.api.global.security.JwtAuthenticationFilter;
import com.budongsan.api.global.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 설정
 *
 * JWT 기반 인증이므로:
 * - 세션 사용 안 함 (STATELESS)
 * - CSRF 비활성화 (JWT 사용 시 불필요)
 * - 공개 URL / 인증 필요 URL 구분
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CSRF 비활성화 (JWT 사용 시 불필요)
                .csrf(AbstractHttpConfigurer::disable)

                // 세션 사용 안 함 (JWT는 STATELESS)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // URL별 접근 권한 설정
                .authorizeHttpRequests(auth -> auth
                        // 인증 없이 접근 가능한 URL
                        .requestMatchers(
                                "/api/auth/**",          // 회원가입, 로그인
                                "/swagger-ui/**",        // Swagger UI
                                "/api-docs/**",          // Swagger API 문서
                                "/",                     // 메인 페이지
                                "/login",                // 웹 로그인 페이지
                                "/signup"                // 웹 회원가입 페이지
                        ).permitAll()
                        // ADMIN만 접근 가능
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        // LEADER만 접근 가능
                        .requestMatchers("/api/teams/create").hasRole("LEADER")
                        // 나머지는 로그인 필요
                        .anyRequest().authenticated()
                )

                // JWT 필터를 UsernamePasswordAuthenticationFilter 앞에 추가
                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtTokenProvider),
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    /**
     * BCrypt 비밀번호 암호화기
     * 회원가입 시 비밀번호 암호화, 로그인 시 비밀번호 검증에 사용
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
