package com.budongsan.core.exception;

import org.springframework.http.HttpStatus;

/**
 * 애플리케이션에서 발생하는 모든 에러 코드를 한 곳에서 관리
 *
 * 구성: (HTTP 상태코드, 에러 메시지)
 *
 * HTTP 상태코드란?
 * - 200: 성공
 * - 400: 클라이언트 잘못된 요청 (ex: 비밀번호 형식 오류)
 * - 401: 인증 실패 (ex: 로그인 필요)
 * - 403: 권한 없음 (ex: 관리자 전용 기능)
 * - 404: 찾을 수 없음 (ex: 없는 매물)
 * - 409: 충돌 (ex: 이미 가입된 이메일)
 * - 500: 서버 내부 오류
 */
public enum ErrorCode {

    // ── 공통 ───────────────────────────────────────────────
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다."),
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "입력값이 올바르지 않습니다."),

    // ── 회원 ───────────────────────────────────────────────
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 회원입니다."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다."),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),

    // ── 토큰 ───────────────────────────────────────────────
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다."),

    // ── 매물 ───────────────────────────────────────────────
    PROPERTY_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 매물입니다."),

    // ── 파일 ───────────────────────────────────────────────
    INVALID_FILE_FORMAT(HttpStatus.BAD_REQUEST, "지원하지 않는 파일 형식입니다."),
    FILE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다.");

    private final HttpStatus httpStatus;  // HTTP 상태 코드
    private final String message;         // 에러 메시지

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public HttpStatus getHttpStatus() { return httpStatus; }
    public String getMessage() { return message; }
}
