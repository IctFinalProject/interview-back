package com.ictedu.surveyfileupload.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ictedu.surveyfileupload.model.entity.UploadedFile;
import com.ictedu.user.model.entity.User;

import java.util.List;

public interface UploadedFileRepository extends JpaRepository<UploadedFile, Long> {
    List<UploadedFile> findByUserOrderByUploadedAtDesc(User user);
}
