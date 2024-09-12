package com.ictedu.bbs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ictedu.bbs.model.entity.BbsReport;
import java.util.List;

public interface BbsReportRepository extends JpaRepository<BbsReport, Long> {
    
    // 댓글에 해당하는 모든 신고 기록 삭제
    void deleteByComment_CommentId(Long commentId);
}