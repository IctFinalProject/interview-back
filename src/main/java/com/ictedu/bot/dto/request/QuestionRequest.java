package com.ictedu.bot.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class QuestionRequest {
    private Long botId;
    private String content;
}
