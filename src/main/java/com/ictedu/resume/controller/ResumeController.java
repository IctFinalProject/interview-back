package com.ictedu.resume.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ictedu.resume.entity.ResumeEntity;
import com.ictedu.resume.service.ResumeService;

@RestController
@RequestMapping("/api/resume")
@CrossOrigin(origins = "http://localhost:3000")
public class ResumeController {

    private final ResumeService resumeService;

    public ResumeController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    @PostMapping
    public ResponseEntity<ResumeEntity> createResume(@RequestBody ResumeEntity resume) {
        ResumeEntity savedResume = resumeService.saveResume(resume);
        return ResponseEntity.ok(savedResume);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResumeEntity> getResume(@PathVariable Long id) {
        ResumeEntity resume = resumeService.getResume(id);
        if (resume != null) {
            return ResponseEntity.ok(resume);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
