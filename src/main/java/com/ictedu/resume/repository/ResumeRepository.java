package com.ictedu.resume.repository;

import com.ictedu.resume.entity.ResumeEntity;
import com.ictedu.user.model.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResumeRepository extends JpaRepository<ResumeEntity, Long> {
    List<ResumeEntity> findByUser(User user);
}
