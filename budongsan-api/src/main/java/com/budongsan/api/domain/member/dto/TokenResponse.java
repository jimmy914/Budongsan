package com.budongsan.api.domain.member.dto;

/**
 * 로그인 성공 시 반환하는 토큰 응답 DTO
 *
 * 클라이언트(JavaFX or 웹)는 이 토큰을 저장해두고
 * 이후 모든 API 요청 시 Header에 accessToken을 담아서 보냄
 * Authorization: Bearer {accessToken}
 */
public record TokenResponse(
        String accessToken,   // 30분짜리 토큰 (API 호출 시 사용)
        String refreshToken   // 7일짜리 토큰 (Access Token 재발급 시 사용)
) {}
