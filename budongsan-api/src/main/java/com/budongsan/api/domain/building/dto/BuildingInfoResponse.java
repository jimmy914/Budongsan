package com.budongsan.api.domain.building.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BuildingInfoResponse {

    private String jimok;
    private Double platArea;
    private Double totArea;
    private String useAprDay;
    private Integer grndFlrCnt;
    private Integer ugrndFlrCnt;
    private String vltnBldYn;
}
