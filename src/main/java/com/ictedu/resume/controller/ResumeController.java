package com.ictedu.resume.controller;

import com.ictedu.resume.entity.ResumeEntity;
import com.ictedu.resume.entity.ResumeProofreadEntity;
import com.ictedu.resume.repository.ResumeProofreadRepository;
import com.ictedu.resume.service.ResumeService;
import com.ictedu.user.model.entity.User;
import com.ictedu.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/resume")
@CrossOrigin(origins = "http://localhost:3000")
public class ResumeController {

    @Autowired
    private ResumeService resumeService;

    @Autowired
    private UserService userService;
    
    @Autowired
    private ResumeProofreadRepository resumeProofreadRepository;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadResume(@RequestParam("email") String email,
                                          @RequestParam("file") MultipartFile file,
                                          @RequestParam("title") String title) {
        try {
            Optional<User> user = userService.findByEmail(email);
            if (user.isPresent()) {
                ResumeEntity savedResume = resumeService.saveResume(file, title, user.get());
                return ResponseEntity.ok(Map.of("message", "이력서가 성공적으로 업로드되었습니다.", "resumeId", savedResume.getResumeId()));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자를 찾을 수 없습니다.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("이력서 업로드 중 오류 발생.");
        }
    }

    @GetMapping("/user-resumes")
    public ResponseEntity<?> getUserResumes(@RequestParam("email") String email) {
        Optional<User> user = userService.findByEmail(email);
        if (user.isPresent()) {
            List<ResumeEntity> resumes = resumeService.findResumesByUser(user.get());
            return ResponseEntity.ok(resumes);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자를 찾을 수 없습니다.");
        }
    }

    @GetMapping("/download/{resumeId}")
    public ResponseEntity<?> downloadResume(@PathVariable Long resumeId) {
        Optional<ResumeEntity> resume = resumeService.findResumeById(resumeId);
        if (resume.isPresent()) {
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=resume.pdf")
                    .body(resume.get().getResumePdf());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("이력서를 찾을 수 없습니다.");
        }
    }

    @DeleteMapping("/delete/{resumeId}")
    public ResponseEntity<?> deleteResume(@PathVariable Long resumeId) {
        try {
            resumeService.deleteResume(resumeId);
            return ResponseEntity.ok("이력서가 성공적으로 삭제되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("이력서 삭제 중 오류 발생.");
        }
    }

    @GetMapping("/proofread/{resumeId}")
    public ResponseEntity<?> getProofread(@PathVariable Long resumeId) {
        Optional<ResumeProofreadEntity> proofread = resumeProofreadRepository.findByResume_ResumeId(resumeId);
        if (proofread.isPresent()) {
            Map<String, String> response = new HashMap<>();
            response.put("selfIntroduction", proofread.get().getSelfIntroduction());
            response.put("motivation", proofread.get().getMotivation());
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("첨삭 정보를 찾을 수 없습니다.");
        }
    }

    @PostMapping("/proofread/save")
    public ResponseEntity<?> saveProofread(@RequestBody Map<String, Object> requestData) {
        Long resumeId = Long.parseLong(requestData.get("resumeId").toString());
        String selfIntroduction = (String) requestData.get("selfIntroduction");
        String motivation = (String) requestData.get("motivation");

        Optional<ResumeEntity> resume = resumeService.findResumeById(resumeId);
        if (resume.isPresent()) {
            resumeService.saveProofread(resume.get(), selfIntroduction, motivation);
            return ResponseEntity.ok("AI 첨삭 정보가 성공적으로 저장되었습니다.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("이력서를 찾을 수 없습니다.");
        }
    }

}
