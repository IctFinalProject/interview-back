package com.ictedu.bot.dto.response;

import com.ictedu.bot.entity.BotAnswerFeedback;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BotAnswerFeedbackResponse {
    private Long feedbackId;
    private Long answerId;
    private Integer likes;
    private Integer dislikes;
    private Boolean isPreferred;  // 새로운 필드 추가

    public BotAnswerFeedbackResponse(BotAnswerFeedback feedback) {
        this.feedbackId = feedback.getFeedbackId();
        this.answerId = feedback.getAnswer().getAnswerId();
        this.likes = feedback.getLikes();
        this.dislikes = feedback.getDislikes();
        this.isPreferred = feedback.getLikes() > feedback.getDislikes();  // 좋아요가 싫어요보다 많으면 선호되는 답변
    }

    // 선호도 점수 계산 메서드 (옵션)
    public double getPreferenceScore() {
        int total = likes + dislikes;
        return total == 0 ? 0 : (double) likes / total;
    }
}