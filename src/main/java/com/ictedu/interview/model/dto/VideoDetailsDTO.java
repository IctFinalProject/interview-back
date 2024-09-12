package com.ictedu.interview.model.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.ictedu.interview.model.entity.ClaudeAnalysis;
import com.ictedu.interview.model.entity.VideoAnalysis;
import com.ictedu.interview.model.entity.VideoEntity;
import com.ictedu.interview.model.entity.VideoSpeechAnalysis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VideoDetailsDTO {
    private List<VideoEntity> videos;
    private List<VideoAnalysis> videoAnalyses;
    private List<VideoSpeechAnalysis> videoSpeechAnalyses;
    private List<ClaudeAnalysis> claudeAnalyses;
}
