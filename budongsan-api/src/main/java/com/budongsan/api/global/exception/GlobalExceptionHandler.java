package com.budongsan.api.global.exception;

import com.budongsan.core.exception.BusinessException;
import com.budongsan.core.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 전역 예외 처리 클래스
 *
 * @RestControllerAdvice = 모든 Controller에서 예외가 발생하면 여기서 한 번에 처리
 *
 * 흐름:
 * Controller에서 예외 발생
 *   → GlobalExceptionHandler가 잡음
 *   → ApiResponse.fail(...) 형태로 응답
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * BusinessException 처리
     * ex) throw new BusinessException(ErrorCode.MEMBER_NOT_FOUND)
     * → 404 { "success": false, "message": "존재하지 않는 회원입니다." }
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<?>> handleBusinessException(BusinessException e) {
        return ResponseEntity
                .status(e.getErrorCode().getHttpStatus())  // ErrorCode에 담긴 HTTP 상태코드 사용
                .body(ApiResponse.fail(e.getMessage()));
    }

    /**
     * 유효성 검사 실패 처리 (@Valid 어노테이션 실패 시)
     * ex) 이메일 형식 오류, 비밀번호 길이 미달 등
     * → 400 { "success": false, "message": "email: 올바른 이메일 형식이 아닙니다." }
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidationException(MethodArgumentNotValidException e) {
        // 첫 번째 에러 필드의 메시지를 가져옴
        FieldError fieldError = e.getBindingResult().getFieldErrors().get(0);
        String message = fieldError.getField() + ": " + fieldError.getDefaultMessage();

        return ResponseEntity
                .badRequest()
                .body(ApiResponse.fail(message));
    }

    /**
     * 그 외 모든 예외 처리 (예상치 못한 서버 에러)
     * → 500 { "success": false, "message": "서버 내부 오류가 발생했습니다." }
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleException(Exception e) {
        return ResponseEntity
                .internalServerError()
                .body(ApiResponse.fail("서버 내부 오류가 발생했습니다."));
    }
}
