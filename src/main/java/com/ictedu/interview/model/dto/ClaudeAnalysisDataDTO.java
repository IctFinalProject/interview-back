package com.ictedu.interview.model.dto;

import lombok.Data;

@Data
public class ClaudeAnalysisDataDTO {
    private ContentAnalysis contentAnalysis;
    private SentimentAnalysis sentimentAnalysis;
    private LanguagePatternAnalysis languagePatternAnalysis;
    private ToneTensionAnalysis toneTensionAnalysis;
    private InsightAnalysis insightAnalysis;
    private String[] keywords;
    private Double overallQuality;
    private String improvementSuggestions;

    @Data
    public static class ContentAnalysis {
        private Double logicScore;
        private String keyIdeas;
        private String specificExamples;
    }

    @Data
    public static class SentimentAnalysis {
        private String tone;
        private Double confidenceScore;
    }

    @Data
    public static class LanguagePatternAnalysis {
        private Double professionalVocabScore;
        private String repetitiveExpressions;
        private Double grammarStructureScore;
    }

    @Data
    public static class ToneTensionAnalysis {
        private Double consistencyScore;
        private String tensionDetected;
    }

    @Data
    public static class InsightAnalysis {
        private Double creativityScore;
        private Double problemSolvingScore;
    }
}
