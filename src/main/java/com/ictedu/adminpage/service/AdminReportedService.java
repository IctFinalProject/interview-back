package com.ictedu.adminpage.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ictedu.adminpage.repository.AdminDeletedCommentRepository;
import com.ictedu.adminpage.repository.AdminReportedCommentRepository;
import com.ictedu.adminpage.repository.AdminReportedPostRepository;
import com.ictedu.bbs.model.entity.Bbs;
import com.ictedu.bbs.model.entity.BbsComment;
import com.ictedu.bbs.model.entity.BbsReport;
import com.ictedu.bbs.repository.BbsCommentRepository;
import com.ictedu.bbs.repository.BbsReportRepository;
import com.ictedu.bbs.repository.BbsRepository;

@Service
public class AdminReportedService {

    private final AdminReportedPostRepository adminReportedPostRepository;
    private final BbsRepository bbsRepository;
    private final AdminDeletedCommentRepository adminDeletedCommentRepository;
    private final AdminReportedCommentRepository reportedCommentRepository;
    private final BbsCommentRepository bbsCommentRepository;
    private final BbsReportRepository bbsReportRepository;

    public AdminReportedService(AdminReportedPostRepository adminReportedPostRepository,
                                BbsRepository bbsRepository,
                                AdminDeletedCommentRepository adminDeletedCommentRepository,
                                AdminReportedCommentRepository reportedCommentRepository,
                                BbsCommentRepository bbsCommentRepository,
                                BbsReportRepository bbsReportRepository) {
        this.adminReportedPostRepository = adminReportedPostRepository;
        this.bbsRepository = bbsRepository;
        this.adminDeletedCommentRepository = adminDeletedCommentRepository;
        this.reportedCommentRepository = reportedCommentRepository;
        this.bbsCommentRepository = bbsCommentRepository;
        this.bbsReportRepository = bbsReportRepository;
    }

    // 모든 신고된 게시글을 조회
    public List<BbsReport> getAllReportedPosts() {
        return adminReportedPostRepository.findAll();
    }

    // 신고된 게시글 상세 조회 메서드 추가
    public BbsReport getReportedPostById(Long reportId) {
        return adminReportedPostRepository.findById(reportId)
                .orElse(null);  // 신고 번호로 조회, 없으면 null 반환
    }

    // 신고된 댓글 목록 조회 메서드
    public List<BbsReport> getAllReportedComments() {
        return reportedCommentRepository.findByCommentIsNotNull();
    }

    // 신고된 게시글 상태 처리
    @Transactional
    public void reportPost(Long reportId) {
        BbsReport bbsReport = adminReportedPostRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("해당 신고 번호가 없습니다: " + reportId));

        Bbs bbs = bbsReport.getBbs();
        bbs.setDeletedReason(1); // 신고로 인한 상태 변경
        bbs.setStatus("HIDDEN");

        bbsReport.setStatus("HIDDEN"); // 신고 상태도 처리
        bbsRepository.save(bbs);
        adminReportedPostRepository.save(bbsReport);
    }

    // 게시글 영구 삭제
    @Transactional
    public void deletePostPermanently(Long reportId) {
        BbsReport bbsReport = adminReportedPostRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("해당 신고 번호가 없습니다: " + reportId));

        Long bbsId = bbsReport.getBbs().getBbsId();

        // 게시글에 연결된 댓글 삭제
        if (bbsReport.getBbs().getComments() != null && !bbsReport.getBbs().getComments().isEmpty()) {
            adminDeletedCommentRepository.deleteByBbs_BbsId(bbsId);
        }

        bbsRepository.deleteById(bbsId);
        adminReportedPostRepository.deleteById(reportId);
    }

    // 댓글 영구 삭제
    @Transactional
    public void deleteCommentPermanently(Long commentId) {
        BbsComment bbsComment = bbsCommentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 없습니다: " + commentId));

        bbsReportRepository.deleteByComment_CommentId(commentId);
        bbsCommentRepository.deleteById(commentId);
    }

    // 신고 상태 업데이트
    @Transactional
    public void updateReportStatus(Long reportId, String status) {
        BbsReport bbsReport = adminReportedPostRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("해당 신고 번호가 없습니다: " + reportId));

        if (bbsReport.getBbs() != null) {
            Bbs bbs = bbsReport.getBbs();
            bbs.setStatus(status);
            bbsRepository.save(bbs);
        }

        if (bbsReport.getComment() != null) {
            BbsComment comment = bbsReport.getComment();
            comment.setStatus(status);
            bbsCommentRepository.save(comment);
        }

        bbsReport.setStatus(status);
        adminReportedPostRepository.save(bbsReport);
    }
}
