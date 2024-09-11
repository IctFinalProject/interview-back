package com.ictedu.interview.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ictedu.interview.model.entity.VideoSpeechAnalysis;

public interface VideoSpeechAnalysisRepository extends JpaRepository<VideoSpeechAnalysis, Long> {
    void deleteByVideoId(Long videoId);
}
