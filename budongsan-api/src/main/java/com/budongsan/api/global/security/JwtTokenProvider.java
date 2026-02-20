package com.budongsan.api.global.security;

import com.budongsan.core.domain.member.MemberRole;
import com.budongsan.core.exception.BusinessException;
import com.budongsan.core.exception.ErrorCode;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 토큰 생성 / 검증 / 파싱 담당
 *
 * JWT 구조:
 * Header.Payload.Signature
 * eyJ... . eyJ... . abc123
 *
 * Payload에 담는 정보 (Claims):
 * - sub   : 이메일 (subject)
 * - role  : 권한 (ADMIN/LEADER/MEMBER)
 * - iat   : 발급 시간
 * - exp   : 만료 시간
 */
@Component
public class JwtTokenProvider {

    private final SecretKey secretKey;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;

    // application.yml의 jwt.* 값들을 자동 주입
    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-expiration}") long accessTokenExpiration,
            @Value("${jwt.refresh-token-expiration}") long refreshTokenExpiration
    ) {
        // 시크릿 키를 바이트로 변환해서 SecretKey 객체 생성
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    /**
     * Access Token 생성 (수명 30분)
     * 이메일과 권한 정보를 토큰에 담음
     */
    public String generateAccessToken(String email, MemberRole role) {
        return Jwts.builder()
                .subject(email)
                .claim("role", role.name())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
                .signWith(secretKey)
                .compact();
    }

    /**
     * Refresh Token 생성 (수명 7일)
     * 이메일만 담음 (최소한의 정보)
     */
    public String generateRefreshToken(String email) {
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + refreshTokenExpiration))
                .signWith(secretKey)
                .compact();
    }

    /**
     * 토큰에서 이메일 추출
     */
    public String getEmail(String token) {
        return parseClaims(token).getSubject();
    }

    /**
     * 토큰에서 권한 추출
     */
    public MemberRole getRole(String token) {
        String role = parseClaims(token).get("role", String.class);
        return MemberRole.valueOf(role);
    }

    /**
     * 토큰 유효성 검증
     * 유효하면 true, 만료/위변조 등 문제 있으면 예외 던짐
     */
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            throw new BusinessException(ErrorCode.EXPIRED_TOKEN);
        } catch (JwtException | IllegalArgumentException e) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }
    }

    /**
     * 토큰 파싱 (서명 검증 + Claims 추출)
     */
    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public long getRefreshTokenExpiration() {
        return refreshTokenExpiration;
    }
}
