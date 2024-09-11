package com.ictedu.interview.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ictedu.interview.model.entity.VideoAnalysis;

public interface VideoAnalysisRepository extends JpaRepository<VideoAnalysis, Long> {
    void deleteByVideoId(Long videoId);
}
