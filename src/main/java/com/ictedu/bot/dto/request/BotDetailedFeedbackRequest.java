package com.ictedu.bot.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class BotDetailedFeedbackRequest {
    private boolean isLike;
    private Integer relevanceScore;
    private Integer clarityScore;
    private String comment;
    private Long userId;
}