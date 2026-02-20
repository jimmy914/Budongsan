package com.budongsan.api.domain.member.controller;

import com.budongsan.api.domain.member.dto.LoginRequest;
import com.budongsan.api.domain.member.dto.SignUpRequest;
import com.budongsan.api.domain.member.dto.TokenResponse;
import com.budongsan.api.domain.member.service.AuthService;
import com.budongsan.core.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * 인증 컨트롤러 (회원가입, 로그인, 토큰 재발급, 로그아웃)
 *
 * @RestController → JSON 응답 반환
 * @RequestMapping → 공통 URL prefix
 * @Tag → Swagger UI에서 그룹 이름
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "인증 API (회원가입/로그인)")
public class AuthController {

    private final AuthService authService;

    /**
     * 회원가입
     * POST /api/auth/signup
     * @Valid → SignUpRequest의 유효성 검사 실행
     */
    @Operation(summary = "회원가입", description = "이메일, 비밀번호, 이름, 역할(LEADER/MEMBER)로 가입")
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Void>> signUp(@Valid @RequestBody SignUpRequest request) {
        authService.signUp(request);
        return ResponseEntity.ok(ApiResponse.success("회원가입이 완료되었습니다."));
    }

    /**
     * 로그인
     * POST /api/auth/login
     */
    @Operation(summary = "로그인", description = "이메일, 비밀번호로 로그인 후 JWT 토큰 반환")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponse>> login(@Valid @RequestBody LoginRequest request) {
        TokenResponse tokenResponse = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("로그인 성공", tokenResponse));
    }

    /**
     * Access Token 재발급
     * POST /api/auth/reissue
     */
    @Operation(summary = "토큰 재발급", description = "Refresh Token으로 새 Access Token 발급")
    @PostMapping("/reissue")
    public ResponseEntity<ApiResponse<TokenResponse>> reissue(@RequestHeader("Refresh-Token") String refreshToken) {
        TokenResponse tokenResponse = authService.reissue(refreshToken);
        return ResponseEntity.ok(ApiResponse.success("토큰이 재발급되었습니다.", tokenResponse));
    }

    /**
     * 로그아웃
     * POST /api/auth/logout
     * @AuthenticationPrincipal → Security Context에서 현재 로그인한 사용자 정보 꺼냄
     */
    @Operation(summary = "로그아웃", description = "Refresh Token 삭제 (재발급 불가)")
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@AuthenticationPrincipal UserDetails userDetails) {
        authService.logout(userDetails.getUsername()); // username = email
        return ResponseEntity.ok(ApiResponse.success("로그아웃 되었습니다."));
    }
}
