package com.ictedu.interview.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/video-analysis")
public class VideoAnalysisController {

	@PostMapping("/result")
	public ResponseEntity<String> receiveAnalysisComplete(@RequestBody Map<String, Object> payload) {
	    Integer videoId = (Integer) payload.get("videoId");
	    String message = (String) payload.get("message");

	    if (videoId == null || message == null) {
	        return ResponseEntity.badRequest().body("Invalid payload");
	    }

	    // 여기서 분석 완료 처리 로직을 구현합니다.
	    // 예를 들어, 데이터베이스에서 해당 videoId의 상태를 업데이트하는 등의 작업을 수행할 수 있습니다.
	   

	    System.out.println("Received analysis complete message for video ID: " + videoId);
	    System.out.println("Message: " + message);

	    return ResponseEntity.ok("Analysis complete message received successfully");
	}
}
