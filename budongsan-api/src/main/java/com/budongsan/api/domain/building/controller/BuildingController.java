package com.budongsan.api.domain.building.controller;

import com.budongsan.api.domain.building.dto.BuildingInfoRequest;
import com.budongsan.api.domain.building.dto.BuildingInfoResponse;
import com.budongsan.api.domain.building.service.BuildingService;
import com.budongsan.core.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/building")
@RequiredArgsConstructor
public class BuildingController {

    private final BuildingService buildingService;

    @PostMapping("/info")
    public ResponseEntity<ApiResponse<BuildingInfoResponse>> getBuildingInfo(
            @Valid @RequestBody BuildingInfoRequest request) {
        BuildingInfoResponse response = buildingService.getBuildingInfo(request.getAddress());
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
