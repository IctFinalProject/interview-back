package com.ictedu.resume.repository;

import com.ictedu.resume.entity.ResumeEntity;
import com.ictedu.resume.entity.ResumeProofreadEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ResumeProofreadRepository extends JpaRepository<ResumeProofreadEntity, Long> {
    Optional<ResumeProofreadEntity> findByResume(ResumeEntity resume);
    Optional<ResumeProofreadEntity> findByResume_ResumeId(Long resumeId);
    void deleteByResume(ResumeEntity resume);
}
