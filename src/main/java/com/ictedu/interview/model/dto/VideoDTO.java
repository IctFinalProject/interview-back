package com.ictedu.interview.model.dto;

import java.time.LocalDateTime;

import com.ictedu.resume.entity.ResumeEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VideoDTO {
    private Long id;
    private String fileName;
    private String filePath;
    private Long userId;
    private Long questionId;
    private String questionText;
    private Long fileSize;
    private LocalDateTime uploadDate;
    private Double answerDuration;
    // 필요에 따라 다른 필드 추가
}
