package com.budongsan.api.domain.team.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 팀 생성 요청 DTO
 */
public record TeamCreateRequest(
        @NotBlank(message = "팀 이름을 입력해주세요.")
        String name
) {}
