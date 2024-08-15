package com.ictedu.bot.dto.response;

import lombok.*;
import java.time.LocalDateTime;
import com.ictedu.bot.entity.BotAnswer;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BotAnswerResponse {
    private Long answerId;
    private Long questionId;
    private String content;
    private LocalDateTime createdTime;

    public BotAnswerResponse(BotAnswer answer) {
        this.answerId = answer.getAnswerId();
        this.questionId = answer.getQuestion().getQuestionId();
        this.content = answer.getContent();
        this.createdTime = answer.getCreatedTime();
    }
}