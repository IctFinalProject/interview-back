package com.ictedu.bbs.service;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommentRequestDto {
    private Long commentId;
    private String content;
    private LocalDateTime createdAt;
    private String username; // 사용자 이름
    private Long userId; // 사용자 ID
}
