package com.ictedu.surveyfileupload.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.ictedu.surveyfileupload.model.entity.UploadedFile;
import com.ictedu.surveyfileupload.service.FileUploadService;
import com.ictedu.surveyfileupload.service.PdfProcessingService;
import com.ictedu.user.model.entity.User;
import com.ictedu.user.repository.UserRepository;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/files")
public class FileUploadController {

    @Autowired
    private FileUploadService fileUploadService;

    @Autowired
    private PdfProcessingService pdfProcessingService;

    @Autowired
    private UserRepository userRepository;

    // 파일 업로드 처리 및 PDF 정보 추출
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file, @RequestParam String email) {
        try {
            // 1. 파일 저장
            fileUploadService.saveUploadedFile(file, email);
            
            // 2. PDF에서 정보 추출
            List<String> extractedInfo = pdfProcessingService.extractInfoFromPDF(file.getBytes());
            
            // 3. 추출된 정보 처리 (여기서는 클라이언트에 반환)
            String extractedText = String.join("\n", extractedInfo);
            return ResponseEntity.ok("Extracted Info:\n" + extractedText);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Failed to upload and process file: " + e.getMessage());
        }
    }

    // 업로드된 파일 조회
    @GetMapping("/uploaded-files")
    public ResponseEntity<List<UploadedFile>> getUploadedFiles(@RequestParam String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            List<UploadedFile> uploadedFiles = fileUploadService.getUploadedFiles(user);
            return ResponseEntity.ok(uploadedFiles);
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }
}
