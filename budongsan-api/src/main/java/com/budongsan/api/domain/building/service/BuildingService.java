package com.budongsan.api.domain.building.service;

import com.budongsan.api.domain.building.dto.BuildingInfoResponse;
import com.budongsan.core.exception.BusinessException;
import com.budongsan.core.exception.ErrorCode;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
@RequiredArgsConstructor
public class BuildingService {

    private final RestTemplate restTemplate;

    @Value("${kakao.api.key}")
    private String kakaoApiKey;

    @Value("${data.go.kr.api.key}")
    private String dataGoKrApiKey;

    @Value("${vworld.api.key}")
    private String vworldApiKey;

    public BuildingInfoResponse getBuildingInfo(String address) {
        // 1. 카카오 주소 검색 (동기)
        JsonNode document = callKakao(address);

        JsonNode addressNode = document.path("address");
        String bCode = addressNode.path("b_code").asText("");
        String bun = addressNode.path("main_address_no").asText("0");
        String jiRaw = addressNode.path("sub_address_no").asText("");
        String ji = jiRaw.isBlank() ? "0" : jiRaw;

        String sigunguCd = bCode.substring(0, 5);
        String bjdongCd = bCode.substring(5, 10);

        int bunInt = 0;
        int jiInt = 0;
        try {
            bunInt = Integer.parseInt(bun);
            jiInt = Integer.parseInt(ji);
        } catch (NumberFormatException ignored) {
        }
        String pnu = bCode + "1" + String.format("%04d", bunInt) + String.format("%04d", jiInt);

        // 2 & 3. 병렬 호출
        final String finalSigunguCd = sigunguCd;
        final String finalBjdongCd = bjdongCd;
        final String finalBun = bun;
        final String finalJi = ji;
        final String finalPnu = pnu;

        CompletableFuture<JsonNode> buildingFuture = CompletableFuture.supplyAsync(
                () -> callBuildingHub(finalSigunguCd, finalBjdongCd, finalBun, finalJi)
        );
        CompletableFuture<String> jimokFuture = CompletableFuture.supplyAsync(
                () -> {
                    try {
                        return callVworld(finalPnu);
                    } catch (Exception e) {
                        return "";
                    }
                }
        );

        JsonNode buildingItem;
        try {
            buildingItem = buildingFuture.get(15, TimeUnit.SECONDS);
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof BusinessException be) throw be;
            throw new BusinessException(ErrorCode.BUILDING_NOT_FOUND);
        } catch (InterruptedException | TimeoutException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException(ErrorCode.BUILDING_NOT_FOUND);
        }

        String jimok = "";
        try {
            jimok = jimokFuture.get(15, TimeUnit.SECONDS);
        } catch (Exception ignored) {
        }

        // 응답 조립
        String platAreaStr = buildingItem.path("platArea").asText(null);
        String totAreaStr = buildingItem.path("totArea").asText(null);
        String useAprDayRaw = buildingItem.path("useAprDay").asText(null);
        int grndFlrCnt = buildingItem.path("grndFlrCnt").asInt(0);
        int ugrndFlrCnt = buildingItem.path("ugrndFlrCnt").asInt(0);
        String vltnBldYnRaw = buildingItem.path("vltnBldYn").asText(null);
        String vltnBldYn = (vltnBldYnRaw == null || vltnBldYnRaw.isBlank()) ? "확인불가" : vltnBldYnRaw;

        return BuildingInfoResponse.builder()
                .jimok(jimok.isBlank() ? null : jimok)
                .platArea(parseDouble(platAreaStr))
                .totArea(parseDouble(totAreaStr))
                .useAprDay(formatDate(useAprDayRaw))
                .grndFlrCnt(grndFlrCnt)
                .ugrndFlrCnt(ugrndFlrCnt)
                .vltnBldYn(vltnBldYn)
                .build();
    }

    /**
     * 카카오 주소 검색 API 호출
     * 검색 결과 첫 번째 document 반환
     */
    private JsonNode callKakao(String address) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + kakaoApiKey);

        String encodedAddress;
        try {
            encodedAddress = URLEncoder.encode(address, StandardCharsets.UTF_8);
        } catch (Exception e) {
            encodedAddress = address;
        }
        String url = "https://dapi.kakao.com/v2/local/search/address.json?query=" + encodedAddress;

        ResponseEntity<JsonNode> response = restTemplate.exchange(
                url, HttpMethod.GET, new HttpEntity<>(headers), JsonNode.class
        );

        JsonNode body = response.getBody();
        if (body == null) throw new BusinessException(ErrorCode.ADDRESS_NOT_FOUND);

        JsonNode documents = body.path("documents");
        if (documents.isEmpty()) throw new BusinessException(ErrorCode.ADDRESS_NOT_FOUND);

        JsonNode document = documents.get(0);
        // address 객체가 없으면 (road_address_only 결과) 실패
        if (document.path("address").isMissingNode() || document.path("address").isNull()) {
            throw new BusinessException(ErrorCode.ADDRESS_NOT_FOUND);
        }
        return document;
    }

    /**
     * 건축HUB 건축물대장 API 호출
     */
    private JsonNode callBuildingHub(String sigunguCd, String bjdongCd, String bun, String ji) {
        try {
            String encodedKey = URLEncoder.encode(dataGoKrApiKey, StandardCharsets.UTF_8);
            String url = "http://apis.data.go.kr/1613000/BldRgstHubService/getBrTitleInfo"
                    + "?serviceKey=" + encodedKey
                    + "&sigunguCd=" + sigunguCd
                    + "&bjdongCd=" + bjdongCd
                    + "&bun=" + bun
                    + "&ji=" + ji
                    + "&_type=json";

            ResponseEntity<JsonNode> response = restTemplate.getForEntity(url, JsonNode.class);
            JsonNode body = response.getBody();
            if (body == null) throw new BusinessException(ErrorCode.BUILDING_NOT_FOUND);

            JsonNode items = body.path("response").path("body").path("items").path("item");
            if (items.isMissingNode() || items.isNull()) {
                throw new BusinessException(ErrorCode.BUILDING_NOT_FOUND);
            }
            // item이 배열이면 첫 번째, 단일 객체면 그대로
            return items.isArray() ? items.get(0) : items;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.BUILDING_NOT_FOUND);
        }
    }

    /**
     * Vworld 토지임야 WFS API 호출 → 지목 반환
     */
    private String callVworld(String pnu) {
        String cqlFilter = "pnu='" + pnu + "'";
        String encodedFilter;
        try {
            encodedFilter = URLEncoder.encode(cqlFilter, StandardCharsets.UTF_8);
        } catch (Exception e) {
            encodedFilter = cqlFilter;
        }

        String url = "https://api.vworld.kr/req/data"
                + "?service=WFS&version=2.0.0&request=GetFeature"
                + "&typeNames=lp_pa_cbnd_bubun"
                + "&key=" + vworldApiKey
                + "&domain=localhost"
                + "&cql_filter=" + encodedFilter
                + "&output=json";

        ResponseEntity<JsonNode> response = restTemplate.getForEntity(url, JsonNode.class);
        JsonNode body = response.getBody();
        if (body == null) return "";

        JsonNode features = body.path("response").path("result")
                .path("featureCollection").path("features");
        if (features.isArray() && !features.isEmpty()) {
            return features.get(0).path("properties").path("lndcgr_code_nm").asText("");
        }
        return "";
    }

    private Double parseDouble(String value) {
        if (value == null || value.isBlank()) return null;
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String formatDate(String raw) {
        if (raw == null || raw.length() < 8) return raw;
        // "20050315" → "2005-03-15"
        return raw.substring(0, 4) + "-" + raw.substring(4, 6) + "-" + raw.substring(6, 8);
    }
}
