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

    // 신고된 댓글 목록 조회 메서드
    public List<BbsReport> getAllReportedComments() {
        return reportedCommentRepository.findByCommentIsNotNull();
    }

    // 특정 ID의 신고된 게시글 조회
    public BbsReport getReportedPostById(Long id) {
        Optional<BbsReport> report = adminReportedPostRepository.findById(id);
        return report.orElse(null);  // ID가 없을 경우 null 반환
    }

    // 신고된 게시글을 처리하는 메서드
    @Transactional
    public void reportPost(Long reportId) {
        BbsReport bbsReport = adminReportedPostRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("해당 신고 번호가 없습니다: " + reportId));

        // 신고된 게시글(Bbs)의 deletedReason을 1로 설정
        Bbs bbs = bbsReport.getBbs();
        bbs.setDeletedReason(1); // 1은 신고된 상태를 의미

        // BbsReport 상태를 HIDDEN으로 업데이트
        bbsReport.setStatus(BbsReport.Status.HIDDEN); // 신고된 상태로 처리

        // 게시글과 신고 상태를 저장
        bbsRepository.save(bbs);
        adminReportedPostRepository.save(bbsReport);
    }

    // 특정 게시글을 영구 삭제
    @Transactional
    public void deletePostPermanently(Long reportId) {
        BbsReport bbsReport = adminReportedPostRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("해당 신고 번호가 없습니다:" + reportId));

        Long bbsId = bbsReport.getBbs().getBbsId();

        // 게시글에 연결된 댓글이 있는 경우, 댓글 삭제
        if (bbsReport.getBbs().getComments() != null && !bbsReport.getBbs().getComments().isEmpty()) {
            adminDeletedCommentRepository.deleteByBbs_BbsId(bbsId);
        }

        // 게시글 삭제
        bbsRepository.deleteById(bbsId);
        // 신고 기록 삭제
        adminReportedPostRepository.deleteById(reportId);
    }

    // 특정 댓글 영구 삭제
    @Transactional
    public void deleteCommentPermanently(Long commentId) {
        BbsComment bbsComment = bbsCommentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 없습니다: " + commentId));

        // 댓글에 대한 모든 신고 기록 삭제
        bbsReportRepository.deleteByComment_CommentId(commentId);

        // 댓글 삭제
        bbsCommentRepository.deleteById(commentId);
    }

    // 신고 상태를 업데이트하는 메서드 (상태 통합 처리)
    @Transactional
    public void updateReportStatus(Long reportId, String status) {
        BbsReport bbsReport = adminReportedPostRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("해당 신고 번호가 없습니다: " + reportId));

        // 게시글 혹은 댓글의 상태 업데이트
        if (bbsReport.getBbs() != null) {
            Bbs bbs = bbsReport.getBbs();
            bbs.setStatus(status);
            bbsRepository.save(bbs); // 게시글 저장
        }

        if (bbsReport.getComment() != null) {
            BbsComment comment = bbsReport.getComment();
            comment.setStatus(status);
            bbsCommentRepository.save(comment); // 댓글 저장
        }

        // 신고 상태 업데이트
        bbsReport.setStatus(BbsReport.Status.valueOf(status.toUpperCase())); // 신고 상태도 변경
        adminReportedPostRepository.save(bbsReport); // 변경된 신고 기록 저장
    }
}
