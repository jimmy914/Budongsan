package com.budongsan.core.exception;

/**
 * 비즈니스 로직에서 발생하는 예외를 처리하는 커스텀 예외 클래스
 *
 * Java 기본 예외(Exception)를 상속받아서 우리만의 예외를 만든 것
 *
 * 사용 예시:
 *   throw new BusinessException(ErrorCode.MEMBER_NOT_FOUND);
 *   throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
 *
 * 이렇게 던지면 GlobalExceptionHandler가 받아서
 * ApiResponse.fail("이미 사용 중인 이메일입니다.") 형태로 응답함
 */
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;  // 어떤 에러인지 담아둠

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());  // 부모 클래스(RuntimeException)에 메시지 전달
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
