package com.ictedu.adminpage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminReportRequestDTO {

    private Long postId;           // 신고된 게시글 ID
    private Long commentId;        // 신고된 댓글 ID (선택적)
    private Long userId;           // 신고자 ID
    private String reason;         // 신고 사유
}