package com.budongsan.api.global.security;

import com.budongsan.core.domain.member.MemberRole;
import com.budongsan.core.exception.BusinessException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * JWT 인증 필터
 *
 * 모든 HTTP 요청을 가로채서 JWT 토큰을 검증
 * OncePerRequestFilter → 요청당 딱 한 번만 실행
 *
 * 흐름:
 * 요청 → 필터 → 토큰 추출 → 검증 → SecurityContext에 인증 정보 저장 → Controller
 */
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // 1. Header에서 토큰 추출
        String token = resolveToken(request);

        // 2. 토큰이 있으면 검증 후 SecurityContext에 저장
        if (token != null) {
            try {
                jwtTokenProvider.validateToken(token);

                String email = jwtTokenProvider.getEmail(token);
                MemberRole role = jwtTokenProvider.getRole(token);

                // Spring Security가 인식할 수 있는 인증 객체 생성
                // "ROLE_" 접두사 필수 (Spring Security 규칙)
                var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
                var userDetails = new User(email, "", authorities);
                var authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, authorities
                );

                // SecurityContext에 인증 정보 저장
                // → 이후 Controller에서 @AuthenticationPrincipal로 꺼낼 수 있음
                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (BusinessException e) {
                // 토큰 만료/위변조 → 인증 정보 없이 진행 (SecurityConfig에서 걸러짐)
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Authorization 헤더에서 Bearer 토큰 추출
     * "Bearer eyJ..." → "eyJ..."
     */
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
