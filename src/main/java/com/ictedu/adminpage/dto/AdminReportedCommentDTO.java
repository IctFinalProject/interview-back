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
public class AdminReportedCommentDTO {

    private Long commentId;            // 댓글 ID
    private String commentContent;     // 댓글 내용
    private String commentAuthor;      // 댓글 작성자
    private LocalDateTime commentCreatedAt; // 댓글 작성 날짜
    private String reportReason;       // 신고 사유
    private String reporterName;       // 신고자 이름
    private LocalDateTime reportedAt;  // 신고 날짜
    private String status;             // 신고 상태 (PENDING, RESOLVED, HIDDEN 등)
}