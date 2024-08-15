package com.ictedu.bot.entity;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Entity
@Table(name = "BOT_FILE")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BotFile {
	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bot_file_seq")
    @SequenceGenerator(name = "bot_file_seq", sequenceName = "BOT_FILE_SEQ", allocationSize = 1)
    private Long fileId;

    @Column(name = "FILE_NAME", nullable = false)
    private String fileName;

    @Column(name = "FILE_PATH", nullable = false)
    private String filePath;

    @Column(name = "UPLOADED_TIME", nullable = false)
    private LocalDateTime uploadedTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BOT_ID")
    private Bot bot;

    @Lob
    @Column(name = "JSON_CONTENT")
    private String jsonContent;

    @PrePersist
    protected void onCreate() {
        uploadedTime = LocalDateTime.now();
    }

    public void saveAsJson(Object data) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        this.jsonContent = objectMapper.writeValueAsString(data);
        this.fileName = "bot_" + bot.getBotId() + "_" + LocalDateTime.now() + ".json";
        this.filePath = "/path/to/json/files/" + this.fileName;
    }
}
