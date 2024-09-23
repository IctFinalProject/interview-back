package com.ictedu.interview.controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.ictedu.interview.model.entity.VideoEntity;
import com.ictedu.interview.service.VideoUploadService;

import java.io.IOException;

@RestController
@RequestMapping("api/interviews")
public class VideoUploadController {

	private static final Logger logger = LoggerFactory.getLogger(VideoUploadController.class);

    @Autowired
    private VideoUploadService videoUploadService;
    
    @Autowired
    private RestTemplate restTemplate;

    @Value("${fastapi.server.url}")
    private String fastApiServerUrl;

    @PostMapping("/upload-video")
    public ResponseEntity<?> uploadVideo(@RequestParam("video") MultipartFile video,
                                         @RequestParam("userId") Long userId,
                                         @RequestParam("questionId") Long questionId,
                                         @RequestParam("choosedResume") String choosedResume,
                                         @RequestParam("questionText") String questionText) {
        logger.info("Received file: {} with size: {}", video.getOriginalFilename(), video.getSize());
        try {
            // 1. Google Drive에 파일 업로드 및 링크 생성
            VideoEntity videoEntity = videoUploadService.processVideo(video, userId, questionId, choosedResume, questionText);

            // 2. FastAPI 서버에 Google Drive 링크를 보내 분석 요청
            String analysisUrl = fastApiServerUrl + "/analyze-video/";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // 요청 바디에 video_id와 Google Drive 링크를 전달
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("video_id", videoEntity.getId());
            requestBody.put("video_link", videoEntity.getFilePath());  // Google Drive 링크 전달

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> fastApiResponse = restTemplate.postForEntity(analysisUrl, request, String.class);

            // 응답 처리
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Video processed successfully");
            response.put("videoId", videoEntity.getId());
            response.put("filePath", videoEntity.getFilePath());  // Google Drive 링크 포함

            if (fastApiResponse.getStatusCode() == HttpStatus.OK) {
                response.put("analysisStatus", "Analysis started");
            } else {
                response.put("analysisStatus", "Failed to start analysis");
                logger.error("Failed to start video analysis: {}", fastApiResponse.getBody());
            }

            return ResponseEntity.ok(response);
        } catch (IOException e) {
            logger.error("Error saving video: ", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Error saving video: " + e.getMessage()));
        } catch (Exception e) {
            logger.error("Error processing video: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error processing video: " + e.getMessage()));
        }
    }
}
