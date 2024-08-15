package com.ictedu.search.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
public class NewRegionCodeController {

    @Value("${external.api.url}")
    private String apiUrl;

    @Value("${external.api.serviceKey}")
    private String serviceKey;

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/api/getRegionCode")
    public ResponseEntity<String> getRegionCode(
            @RequestParam String locatadd_nm,
            @RequestParam(defaultValue = "1") String pageNo,
            @RequestParam(defaultValue = "100") String numOfRows,
            @RequestParam(defaultValue = "JSON") String type) {

        System.out.println("TEST1: getRegionCode called");
        try {
            String encodedLocataddNm = URLEncoder.encode(locatadd_nm, StandardCharsets.UTF_8.toString());
            URI uri = new URI(String.format("%s?serviceKey=%s&pageNo=%s&numOfRows=%s&type=%s&locatadd_nm=%s",
                    apiUrl, serviceKey, pageNo, numOfRows, type, encodedLocataddNm));

            System.out.println("TEST2: URL constructed: " + uri.toString());

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);
            System.out.println("TEST3: Response received: " + response.getBody());

            // JSON 응답을 그대로 반환
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            System.out.println("TEST5: Error: " + e.getMessage());
            return ResponseEntity.badRequest().body("Invalid URI or encoding error: " + e.getMessage());
        }
    }
}

