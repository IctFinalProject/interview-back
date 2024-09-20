package com.ictedu.adminpage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminReportedPostDTO {

    private Long postId;                // 게시글 ID
    private String postTitle;           // 게시글 제목
    private String postAuthor;          // 게시글 작성자
    private LocalDateTime postCreatedAt;// 게시글 작성 날짜
    private String postContent;         // 게시글 내용
    private String reportReason;        // 신고 사유
    private String reporterName;        // 신고자 이름
    private LocalDateTime reportedAt;   // 신고 날짜
    private String status;              // 신고 상태 (PENDING, RESOLVED, HIDDEN 등)
}

