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

	    return ResponseEntity.ok("Analysis complete message received successfully");
	}
}
