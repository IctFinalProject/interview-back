package com.ictedu.bot.dto.response;

import lombok.*;
import java.time.LocalDateTime;
import com.ictedu.bot.entity.BotQuestion;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BotQuestionResponse {
    private Long questionId;
    private Long botId;
    private String content;
    private LocalDateTime createdTime;

    public static BotQuestionResponse from(BotQuestion question) {
        return BotQuestionResponse.builder()
                .questionId(question.getQuestionId())
                .botId(question.getBot().getBotId())
                .content(question.getContent())
                .createdTime(question.getCreatedTime())
                .build();
    }
}