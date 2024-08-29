package com.ictedu.bot.dto.response;

import lombok.*;

import java.time.LocalDateTime;

import com.ictedu.bot.entity.Bot;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BotResponse {
    private Long botId;
    private Long id;
    private LocalDateTime createdTime;
    private LocalDateTime lastUpdatedTime;
    private LocalDateTime endedTime;
    
    public static BotResponse from(Bot bot) {
        return BotResponse.builder()
            .botId(bot.getBotId())
            .id(bot.getId())
            .createdTime(bot.getCreatedTime())
            .lastUpdatedTime(bot.getLastUpdatedTime())
            .endedTime(bot.getEndedTime())
            .build();
    }
}