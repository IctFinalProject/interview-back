package com.ictedu.interview.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ictedu.interview.model.entity.VideoSpeechAnalysis;
import com.ictedu.interview.model.entity.VideoEntity;

public interface VideoSpeechAnalysisRepository extends JpaRepository<VideoSpeechAnalysis, Long> {
    void deleteByVideo(VideoEntity video);
}