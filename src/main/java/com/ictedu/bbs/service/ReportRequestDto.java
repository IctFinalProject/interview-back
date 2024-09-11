package com.ictedu.bbs.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.Map;

@Data
@AllArgsConstructor
public class ReportRequestDto {

    private Long postId;        // 게시물 ID
    private Long commentId;     // 댓글 ID
    private String reason;      // 신고 사유
    private Map<String, Boolean> additionalInfo;  // 추가 정보
    private String userId;       // 신고자 ID
    
    // 기본 생성자
    public ReportRequestDto() {}

    // 게터와 세터
    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public Long getCommentId() {
        return commentId;
    }

    public void setCommentId(Long commentId) {
        this.commentId = commentId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Map<String, Boolean> getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(Map<String, Boolean> additionalInfo) {
        this.additionalInfo = additionalInfo;
    }
}