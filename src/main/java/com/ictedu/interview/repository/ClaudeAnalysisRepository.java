package com.ictedu.interview.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ictedu.interview.model.entity.ClaudeAnalysis;
import com.ictedu.interview.model.entity.VideoEntity;
import com.ictedu.interview.model.entity.VideoSpeechAnalysis;

@Repository
public interface ClaudeAnalysisRepository extends JpaRepository<ClaudeAnalysis, Long>{
	void deleteByVideo(VideoEntity video);	
	List<ClaudeAnalysis> findAllByVideoId(Long videoIdLong);
}
