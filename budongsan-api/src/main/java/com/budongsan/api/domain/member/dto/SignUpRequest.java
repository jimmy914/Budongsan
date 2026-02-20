package com.budongsan.api.domain.member.dto;

import com.budongsan.core.domain.member.MemberRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 회원가입 요청 DTO
 *
 * DTO(Data Transfer Object) = 계층 간 데이터 전달용 객체
 * Controller에서 클라이언트 요청 데이터를 받을 때 사용
 *
 * @NotBlank → null, 빈 문자열, 공백만 있는 문자열 모두 거부
 * @Email    → 이메일 형식 검증
 * @Size     → 길이 제한
 */
public record SignUpRequest(

        @NotBlank(message = "이메일을 입력해주세요.")
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        String email,

        @NotBlank(message = "비밀번호를 입력해주세요.")
        @Size(min = 8, message = "비밀번호는 8자 이상이어야 합니다.")
        String password,

        @NotBlank(message = "이름을 입력해주세요.")
        String name,

        @NotNull(message = "역할을 선택해주세요.")
        MemberRole role  // LEADER 또는 MEMBER (ADMIN은 직접 DB에서 설정)
) {}
