package com.ictedu.bot.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FeedbackAnalysisResponse {
    private double avgRelevanceScore;
    private double avgClarityScore;
    private int totalFeedbacks;
    private long totalLikes;
    private long totalDislikes;
}