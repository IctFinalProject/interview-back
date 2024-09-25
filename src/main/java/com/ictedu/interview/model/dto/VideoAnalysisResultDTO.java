package com.ictedu.interview.model.dto;

import lombok.Data;

@Data
public class VideoAnalysisResultDTO {
    private Long videoId;
    private VideoAnalysisDTO videoAnalysis;
    private SpeechAnalysisDTO speechAnalysis;
    private ClaudeAnalysisDTO claudeAnalysis;
    private Double analysisTime;

    @Data
    public static class VideoAnalysisDTO {
        private Double headEyeHeadScore;
        private Double headEyeEyeScore;
        private Double poseScore;
        private Double avgScore;
        private String analyzedFilePath;
        private AudioAnalysisDTO audioAnalysis;
        private String headEyeMessage;
        private String poseMessage;
        private String headEyeFeedback;
        
    }

    @Data
    public static class AudioAnalysisDTO {
        private Double averagePitch;
        private Double tempo;
        private Double averageVolume;
        private Double averageSpectralCentroid;
        private String audioFeedback;
    }

    @Data
    public static class SpeechAnalysisDTO {
        private String transcription;
        private SentimentAnalysisDTO sentimentAnalysis;
        private String textSummary;
    }

    @Data
    public static class SentimentAnalysisDTO {
        private DocumentSentimentDTO document;
    }

    @Data
    public static class DocumentSentimentDTO {
        private String sentiment;
        private ConfidenceDTO confidence;
    }

    @Data
    public static class ConfidenceDTO {
        private Double negative;
        private Double positive;
        private Double neutral;
    }

    @Data
    public static class ClaudeAnalysisDTO {
        private ClaudeAnalysisDataDTO analysisData;
        private Double overallQuality;
        private String improvementSuggestions;
        private String[] keywords;
    }
}