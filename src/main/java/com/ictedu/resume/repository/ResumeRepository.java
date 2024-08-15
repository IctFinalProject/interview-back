package com.ictedu.resume.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ictedu.resume.entity.ResumeEntity;

public interface ResumeRepository extends JpaRepository<ResumeEntity, Long> {
    // 추가적인 쿼리 메소드가 필요하다면 여기에 정의합니다.
}
