package com.budongsan.core.response;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 모든 API 응답에 사용되는 공통 응답 포맷
 *
 * 예시:
 * 성공: { "success": true, "message": "로그인 성공", "data": { ... } }
 * 실패: { "success": false, "message": "비밀번호가 틀렸습니다", "data": null }
 *
 * @param <T> 응답 데이터 타입 (제네릭) - 어떤 타입이든 담을 수 있음
 */
@JsonInclude(JsonInclude.Include.NON_NULL) // data가 null이면 JSON에 아예 포함하지 않음
public class ApiResponse<T> {

    private final boolean success;   // 성공 여부
    private final String message;    // 응답 메시지
    private final T data;            // 실제 데이터 (제네릭)

    // 생성자는 private → 외부에서 직접 new 못 함, 아래 정적 메서드로만 생성
    private ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    // ── 성공 응답 ──────────────────────────────────────────

    /** 데이터 + 메시지 둘 다 있는 성공 응답 */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }

    /** 데이터만 있는 성공 응답 (메시지 기본값) */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "요청이 성공적으로 처리되었습니다.", data);
    }

    /** 메시지만 있는 성공 응답 (데이터 없을 때, ex: 삭제 성공) */
    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(true, message, null);
    }

    // ── 실패 응답 ──────────────────────────────────────────

    /** 에러 메시지만 있는 실패 응답 */
    public static <T> ApiResponse<T> fail(String message) {
        return new ApiResponse<>(false, message, null);
    }

    // ── Getter (Jackson이 JSON 변환할 때 필요) ─────────────

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public T getData() { return data; }
}
