package com.ictedu.resume.service;

import com.ictedu.resume.entity.ResumeEntity;
import com.ictedu.resume.entity.ResumeProofreadEntity;
import com.ictedu.resume.repository.ResumeProofreadRepository;
import com.ictedu.resume.repository.ResumeRepository;
import com.ictedu.user.model.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ResumeService {

    @Autowired
    private ResumeRepository resumeRepository;

    @Autowired
    private ResumeProofreadRepository proofreadRepository;
    
    @Transactional
    public ResumeEntity saveResume(MultipartFile file, String title, User user) throws IOException {
        ResumeEntity resumeEntity = ResumeEntity.builder()
                .resumePdf(file.getBytes())
                .title(title)
                .user(user)
                .createdDate(LocalDateTime.now())
                .build();
        return resumeRepository.save(resumeEntity);  // 저장된 ResumeEntity를 반환
    }
    
    @Transactional
    public void saveProofread(ResumeEntity resume, String selfIntroduction, String motivation) {
        ResumeProofreadEntity proofreadEntity = ResumeProofreadEntity.builder()
                .resume(resume)
                .selfIntroduction(selfIntroduction)
                .motivation(motivation)
                .build();
        proofreadRepository.save(proofreadEntity);
    }
    

    public List<ResumeEntity> findResumesByUser(User user) {
        return resumeRepository.findByUser(user);
    }

    public Optional<ResumeEntity> findResumeById(Long resumeId) {
        return resumeRepository.findById(resumeId);
    }

    @Transactional
    public void deleteResume(Long resumeId) {
        Optional<ResumeEntity> resumeOpt = resumeRepository.findById(resumeId);
        if (resumeOpt.isPresent()) {
            ResumeEntity resume = resumeOpt.get();

            // 먼저 자식 엔터티(ResumeProofreadEntity)를 삭제합니다.
            proofreadRepository.deleteByResume(resume);

            // 그런 다음 부모 엔터티(ResumeEntity)를 삭제합니다.
            resumeRepository.delete(resume);
        }
    }

    
    public Optional<ResumeProofreadEntity> getProofreadByResume(ResumeEntity resume) {
        return proofreadRepository.findByResume(resume);
    }
    public Optional<ResumeProofreadEntity> getProofreadByResumeId(Long resumeId) {
        return proofreadRepository.findByResume_ResumeId(resumeId);
    }
}
