package com.ictedu.search.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/search")
public class CorpInfoController {

    @Value("${external.api.serviceKey}")
    private String serviceKey;

    @Value("${kakao.api.key}")
    private String kakaoApiKey;

    @GetMapping("/corpInfo")
    public ResponseEntity<String> getCorpInfo(
            @RequestParam String pageNo,
            @RequestParam String numOfRows,
            @RequestParam String resultType,
            @RequestParam(required = false) String crno,
            @RequestParam(required = false) String corpNm,
            @RequestParam(required = false) String selectedRegion,
            @RequestParam(required = false) List<String> selectedDistricts,
            @RequestParam(required = false) List<String> selectedSizes,
            @RequestParam(required = false) List<String> selectedSalaries
    ) {

        // 요청 URL과 기본 쿼리 파라미터 설정
        String url = "https://apis.data.go.kr/1160100/service/GetCorpBasicInfoService_V2/getCorpOutline_V2";
        StringBuilder queryParams = new StringBuilder("?serviceKey=" + serviceKey
                + "&pageNo=" + pageNo
                + "&numOfRows=" + numOfRows
                + "&resultType=" + resultType);

        // 선택적 파라미터 설정
        if (crno != null && !crno.isEmpty()) {
            queryParams.append("&crno=").append(crno);
        }
        if (corpNm != null && !corpNm.isEmpty()) {
            String encodedCorpNm = URLEncoder.encode(corpNm, StandardCharsets.UTF_8);
            queryParams.append("&corpNm=").append(encodedCorpNm);
        }
        if (selectedSizes != null && !selectedSizes.isEmpty()) {
            String sizesParam = selectedSizes.stream()
                .map(size -> URLEncoder.encode(size, StandardCharsets.UTF_8))
                .collect(Collectors.joining(","));
            queryParams.append("&selectedSizes=").append(sizesParam);
        }
        if (selectedSalaries != null && !selectedSalaries.isEmpty()) {
            String salariesParam = selectedSalaries.stream()
                .map(salary -> URLEncoder.encode(salary, StandardCharsets.UTF_8))
                .collect(Collectors.joining(","));
            queryParams.append("&selectedSalaries=").append(salariesParam);
        }


        // URI 클래스를 사용하여 URL 생성
        URI uri;
        try {
            uri = new URI(url + queryParams.toString());
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Invalid URI");
        }

        // RestTemplate을 사용하여 GET 요청 보내기
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response;
        try {
            response = restTemplate.getForEntity(uri, String.class);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("API 요청 오류: " + e.getMessage());
        }

        // 응답 데이터를 필터링 시작
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> responseData;
        try {
            responseData = objectMapper.readValue(responseBody, new TypeReference<Map<String, Object>>() {});
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("JSON 처리 오류: " + e.getMessage());
        }

        Map<String, Object> responseMap = (Map<String, Object>) responseData.get("response");
        Map<String, Object> bodyMap = (Map<String, Object>) responseMap.get("body");
        Map<String, Object> itemsMap = (Map<String, Object>) bodyMap.get("items");
        List<Map<String, Object>> items = (List<Map<String, Object>>) itemsMap.get("item");

        if (items == null || items.isEmpty()) {
            return ResponseEntity.ok("No data found");
        }

        List<Map<String, Object>> filteredItems = items.stream()
                .filter(item -> {
                    // 필터 추가: 회사명 일치 여부 확인
                    if (corpNm != null && !corpNm.isEmpty()) {
                        String companyName = item.get("corpNm").toString();
                        return companyName.contains(corpNm); // 회사명 부분 일치 필터링
                    }
                    return true;
                })
                .filter(item -> {
                    if (selectedRegion != null && !selectedRegion.isEmpty()) {
                        if (!item.get("enpBsadr").toString().contains(selectedRegion)) {
                            return false;
                        }
                    }
                    if (selectedDistricts != null && !selectedDistricts.isEmpty()) {
                        boolean districtMatch = selectedDistricts.stream()
                                .anyMatch(district -> item.get("enpBsadr").toString().contains(district));
                        if (!districtMatch) {
                            return false;
                        }
                    }
                    return true;
                })
                .filter(item -> {
                    if (selectedSizes == null || selectedSizes.isEmpty()) return true;
                    int empCount = Integer.parseInt(item.get("enpEmpeCnt").toString());
                    return selectedSizes.stream().anyMatch(size -> {
                        switch (size) {
                            case "미공개":
                                return empCount == 0;
                            case "스타트업":
                                return empCount >= 1 && empCount < 10;
                            case "소기업":
                                return empCount >= 10 && empCount < 50;
                            case "중소기업":
                                return empCount >= 50 && empCount < 250;
                            case "대기업":
                                return empCount >= 250;
                            default:
                                return true;
                        }
                    });
                })
                .filter(item -> {
                    if (selectedSalaries == null || selectedSalaries.isEmpty()) return true;
                    Object salaryObj = item.get("enpPn1AvgSlryAmt");
                    if (salaryObj == null) {
                        return false;
                    }

                    String salaryStr = salaryObj.toString().trim();
                    if (salaryStr.isEmpty()) {
                        return false;
                    }

                    try {
                        long salary = Long.parseLong(salaryStr);
                        boolean matches = selectedSalaries.stream().anyMatch(salaryRange -> {
                            switch (salaryRange) {
                                case "연봉협의":
                                    return salary == 0;
                                case "연봉 3000만원이상 6000만원 미만":
                                    return salary >= 30000000 && salary < 60000000;
                                case "연봉 6000만원이상 1억 미만":
                                    return salary >= 60000000 && salary < 100000000;
                                case "연봉 1억이상":
                                    return salary >= 100000000;
                                default:
                                    return true;
                            }
                        });

                        return matches;

                    } catch (NumberFormatException e) {
                        System.err.println("Invalid salary format: " + salaryStr);
                        return false;
                    }
                })
                .map(item -> {
                    Map<String, Object> newItem = new HashMap<>(); // 수정 가능한 맵으로 변경
                    newItem.put("corpNm", item.get("corpNm"));
                    newItem.put("enpOzpno", item.get("enpOzpno"));
                    newItem.put("enpBsadr", item.get("enpBsadr"));
                    newItem.put("enpDtadr", item.get("enpDtadr"));
                    newItem.put("enpEmpeCnt", item.get("enpEmpeCnt"));
                    newItem.put("enpPn1AvgSlryAmt", item.get("enpPn1AvgSlryAmt"));

                    // 주소를 위도와 경도로 변환
                    String address = item.get("enpBsadr").toString() + " " + item.get("enpDtadr").toString();
                    double[] coords = getCoordinatesFromAddress(address);
                    newItem.put("latitude", coords[0]);
                    newItem.put("longitude", coords[1]);

                    return newItem;
                })
                .collect(Collectors.toList());

        // 필터링된 데이터를 JSON 형식으로 변환
        try {
            responseBody = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(filteredItems);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("JSON 변환 오류: " + e.getMessage());
        }

        // 응답 반환
        return ResponseEntity.ok(responseBody);
    }

    private double[] getCoordinatesFromAddress(String address) {
        String apiUrl = "https://dapi.kakao.com/v2/local/search/address.json";
        String apiKey = "KakaoAK " + kakaoApiKey;

        RestTemplate restTemplate = new RestTemplate();
        URI uri;
        try {
            uri = new URI(apiUrl + "?query=" + URLEncoder.encode(address, StandardCharsets.UTF_8));
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return new double[]{0.0, 0.0};
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", apiKey);
        headers.set("KA", "sdk/1.0.0 os/java lang/java"); // KA 헤더 추가
        headers.set("Content-Type", "application/json;charset=UTF-8");
        headers.set("Accept", "application/json;charset=UTF-8");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response;
        try {
            response = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);
        } catch (Exception e) {
            e.printStackTrace();
            return new double[]{0.0, 0.0};
        }

        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> responseData;
        try {
            responseData = objectMapper.readValue(responseBody, new TypeReference<Map<String, Object>>() {});
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return new double[]{0.0, 0.0};
        }

        List<Map<String, Object>> documents = (List<Map<String, Object>>) responseData.get("documents");
        if (documents.isEmpty()) {
            return new double[]{0.0, 0.0};
        }

        Map<String, Object> document = documents.get(0);
        double lat = Double.parseDouble(document.get("y").toString());
        double lng = Double.parseDouble(document.get("x").toString());

        return new double[]{lat, lng};
    }
}