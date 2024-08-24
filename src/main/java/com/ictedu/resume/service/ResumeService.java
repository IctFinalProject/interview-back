package com.ictedu.resume.service;

import com.ictedu.resume.entity.ResumeEntity;
import com.ictedu.resume.repository.ResumeRepository;
import com.ictedu.user.model.entity.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ResumeService {

    @Autowired
    private ResumeRepository resumeRepository;

    public void saveResume(MultipartFile file, String title, User user) throws IOException {
        ResumeEntity resumeEntity = ResumeEntity.builder()
                .resumePdf(file.getBytes())
                .title(title)
                .user(user)
                .createdDate(LocalDateTime.now())  // 현재 날짜와 시간을 설정
                .build();
        resumeRepository.save(resumeEntity);
    }

    public List<ResumeEntity> findResumesByUser(User user) {
        return resumeRepository.findByUser(user);
    }

    public Optional<ResumeEntity> findResumeById(Long resumeId) {
        return resumeRepository.findById(resumeId);
    }

    public void deleteResume(Long resumeId) {
        resumeRepository.deleteById(resumeId);
    }
}
