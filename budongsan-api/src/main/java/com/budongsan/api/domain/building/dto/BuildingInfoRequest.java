package com.budongsan.api.domain.building.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class BuildingInfoRequest {

    @NotBlank(message = "주소를 입력해주세요")
    private String address;
}
