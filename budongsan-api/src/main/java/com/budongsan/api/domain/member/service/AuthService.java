package com.budongsan.api.domain.member.service;

import com.budongsan.api.domain.member.dto.LoginRequest;
import com.budongsan.api.domain.member.dto.SignUpRequest;
import com.budongsan.api.domain.member.dto.TokenResponse;
import com.budongsan.api.domain.member.repository.MemberRepository;
import com.budongsan.api.global.security.JwtTokenProvider;
import com.budongsan.core.domain.member.Member;
import com.budongsan.core.exception.BusinessException;
import com.budongsan.core.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

/**
 * 인증 서비스 (회원가입, 로그인, 토큰 재발급, 로그아웃)
 *
 * @Service     → Spring이 이 클래스를 서비스 빈으로 관리
 * @Transactional → DB 작업 중 오류 나면 자동 롤백
 * @RequiredArgsConstructor → final 필드 생성자 자동 생성 (의존성 주입)
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;       // BCrypt 암호화
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, String> redisTemplate;

    // Redis Key 접두사 (RT = Refresh Token)
    private static final String RT_PREFIX = "RT:";

    /**
     * 회원가입
     * 1. 이메일 중복 체크
     * 2. 비밀번호 BCrypt 암호화
     * 3. DB 저장
     */
    @Transactional
    public void signUp(SignUpRequest request) {
        // 이메일 중복 체크
        if (memberRepository.existsByEmail(request.email())) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
        }

        // 비밀번호 암호화 후 회원 저장
        Member member = Member.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password())) // BCrypt 암호화
                .name(request.name())
                .role(request.role())
                .build();

        memberRepository.save(member);
    }

    /**
     * 로그인
     * 1. 이메일로 회원 조회
     * 2. 비밀번호 검증
     * 3. Access Token + Refresh Token 발급
     * 4. Refresh Token Redis에 저장
     */
    @Transactional(readOnly = true)
    public TokenResponse login(LoginRequest request) {
        // 회원 조회
        Member member = memberRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        // 비밀번호 검증 (입력한 비번 vs 암호화된 비번 비교)
        if (!passwordEncoder.matches(request.password(), member.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_PASSWORD);
        }

        // 토큰 생성
        String accessToken = jwtTokenProvider.generateAccessToken(member.getEmail(), member.getRole());
        String refreshToken = jwtTokenProvider.generateRefreshToken(member.getEmail());

        // Refresh Token Redis에 저장 (Key: "RT:이메일", Value: refreshToken, 만료: 7일)
        redisTemplate.opsForValue().set(
                RT_PREFIX + member.getEmail(),
                refreshToken,
                jwtTokenProvider.getRefreshTokenExpiration(),
                TimeUnit.MILLISECONDS
        );

        return new TokenResponse(accessToken, refreshToken);
    }

    /**
     * Access Token 재발급
     * 1. Refresh Token 유효성 검증
     * 2. Redis에 저장된 Refresh Token과 비교
     * 3. 새 Access Token 발급
     */
    public TokenResponse reissue(String refreshToken) {
        // Refresh Token 검증
        jwtTokenProvider.validateToken(refreshToken);

        String email = jwtTokenProvider.getEmail(refreshToken);

        // Redis에서 Refresh Token 조회
        String savedRefreshToken = redisTemplate.opsForValue().get(RT_PREFIX + email);

        if (savedRefreshToken == null || !savedRefreshToken.equals(refreshToken)) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }

        // 회원 정보 조회 (권한 정보 필요)
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        // 새 Access Token 발급
        String newAccessToken = jwtTokenProvider.generateAccessToken(member.getEmail(), member.getRole());

        return new TokenResponse(newAccessToken, refreshToken);
    }

    /**
     * 로그아웃
     * Redis에서 Refresh Token 삭제 → 재발급 불가 상태로 만듦
     */
    public void logout(String email) {
        redisTemplate.delete(RT_PREFIX + email);
    }
}
