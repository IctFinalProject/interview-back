package com.ictedu.bbs.service;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BbsCommentDTO {

    private Long commentId;        // 댓글 ID
    private String content;        // 댓글 내용
    private String username;       // 작성자 이름
    private String bbsTitle;       // 게시글 제목
    private LocalDateTime createdAt;  // 댓글 생성 날짜
    private LocalDateTime deletedAt;  // 댓글 삭제 날짜

    // Lombok이 자동으로 생성자, getter, setter를 생성해줌
}