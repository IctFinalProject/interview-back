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
}
