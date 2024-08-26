package com.ictedu.surveyfileupload.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ictedu.surveyfileupload.model.entity.UploadedFile;
import com.ictedu.surveyfileupload.repository.UploadedFileRepository;
import com.ictedu.user.model.entity.User;
import com.ictedu.user.repository.UserRepository;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class FileUploadService {

    @Autowired
    private UploadedFileRepository uploadedFileRepository;

    @Autowired
    private UserRepository userRepository;

    // 파일 저장 메소드
    public void saveUploadedFile(MultipartFile file, String email) throws IOException {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            UploadedFile uploadedFile = UploadedFile.builder()
                    .user(user)
                    .fileName(file.getOriginalFilename())
                    .fileData(file.getBytes())
                    .build();

            uploadedFileRepository.save(uploadedFile);
        } else {
            throw new IllegalArgumentException("User not found with email: " + email);
        }
    }

    // 특정 유저의 파일 조회 메소드
    public List<UploadedFile> getUploadedFiles(User user) {
        return uploadedFileRepository.findByUserOrderByUploadedAtDesc(user);
    }
}
