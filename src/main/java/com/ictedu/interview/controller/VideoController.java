package com.ictedu.interview.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ictedu.interview.service.InterviewService;

@RestController
@RequestMapping("/api/video")
public class VideoController {

    @Autowired
    private InterviewService interviewService;

    @DeleteMapping("/{videoId}")
    public ResponseEntity<?> deleteVideo(@PathVariable Long videoId) {
        try {
        	interviewService.deleteVideoById(videoId);
            return ResponseEntity.ok("Video 및 관련 데이터를 성공적으로 삭제했습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("삭제 중 오류가 발생했습니다.");
        }
    }
}
