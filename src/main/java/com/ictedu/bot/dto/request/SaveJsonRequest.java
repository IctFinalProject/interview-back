package com.ictedu.bot.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SaveJsonRequest {
    private Long botId;
    private Object data;
}