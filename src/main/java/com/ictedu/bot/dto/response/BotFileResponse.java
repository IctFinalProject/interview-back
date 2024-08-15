package com.ictedu.bot.dto.response;

import lombok.*;
import java.time.LocalDateTime;
import com.ictedu.bot.entity.BotFile;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BotFileResponse {
    private Long fileId;
    private Long botId;
    private String fileName;
    private String filePath;
    private LocalDateTime uploadedTime;

    public BotFileResponse(BotFile file) {
        this.fileId = file.getFileId();
        this.botId = file.getBot().getBotId();
        this.fileName = file.getFileName();
        this.filePath = file.getFilePath();
        this.uploadedTime = file.getUploadedTime();
    }
}