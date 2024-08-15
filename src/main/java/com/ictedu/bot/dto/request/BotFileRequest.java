package com.ictedu.bot.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class BotFileRequest {
    private Long botId;
    private String fileName;
    private String filePath;
    private String jsonContent;
	
}