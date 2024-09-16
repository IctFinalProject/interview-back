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

    // 특정 게시글을 영구 삭제
    @Transactional
    public void deletePostPermanently(Long reportId) {
        BbsReport bbsReport = adminReportedPostRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("해당 신고 번호가 없습니다:" + reportId));

        Long bbsId = bbsReport.getBbs().getBbs_id();

        if (bbsReport.getBbs().getComments() != null && !bbsReport.getBbs().getComments().isEmpty()) {
            adminDeletedCommentRepository.deleteByBbs_BbsId(bbsId);
        }

        bbsRepository.deleteById(bbsId);
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

    // 게시글 숨기기 처리 (status를 HIDDEN으로 설정)
    @Transactional
    public void hideReportedPost(Long reportId) {
        BbsReport bbsReport = adminReportedPostRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("해당 신고 번호가 없습니다: " + reportId));

        // 게시글의 상태를 HIDDEN으로 설정
        bbsReport.getBbs().setStatus("HIDDEN");

        // 변경 사항 저장
        bbsRepository.save(bbsReport.getBbs());
    }

    // 게시글 복구 처리 (status를 VISIBLE으로 설정)
    @Transactional
    public void restoreReportedPost(Long reportId) {
        BbsReport bbsReport = adminReportedPostRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("해당 신고 번호가 없습니다: " + reportId));

        // 게시글의 상태를 VISIBLE로 설정
        bbsReport.getBbs().setStatus("VISIBLE");

        // 변경 사항 저장
        bbsRepository.save(bbsReport.getBbs());
    }

    // 댓글 숨기기 처리 (status를 HIDDEN으로 설정)
    @Transactional
    public void hideReportedComment(Long reportId) {
        BbsReport bbsReport = adminReportedPostRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("해당 신고 번호가 없습니다: " + reportId));

        // 댓글의 상태를 HIDDEN으로 설정
        bbsReport.getComment().setStatus("HIDDEN");

        // 변경 사항 저장
        bbsCommentRepository.save(bbsReport.getComment());
    }

    // 댓글 복구 처리 (status를 VISIBLE으로 설정)
    @Transactional
    public void restoreReportedComment(Long reportId) {
        BbsReport bbsReport = adminReportedPostRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("해당 신고 번호가 없습니다: " + reportId));

        // 댓글의 상태를 VISIBLE로 설정
        bbsReport.getComment().setStatus("VISIBLE");

        // 변경 사항 저장
        bbsCommentRepository.save(bbsReport.getComment());
    }

    // 게시글 상태 업데이트
    @Transactional
    public void updatePostStatus(Long reportId, String status) {
        BbsReport bbsReport = adminReportedPostRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("해당 신고 번호가 없습니다: " + reportId));

        // 게시글의 상태 업데이트
        Bbs bbs = bbsReport.getBbs();
        bbs.setStatus(status);

        // 게시글 저장
        bbsRepository.save(bbs);
    }

    // 댓글 상태 업데이트
    @Transactional
    public void updateCommentStatus(Long reportId, String status) {
        BbsReport bbsReport = adminReportedPostRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("해당 신고 번호가 없습니다: " + reportId));

        BbsComment comment = bbsReport.getComment();
        comment.setStatus(status);

        // 댓글 저장
        bbsCommentRepository.save(comment);
    }
}
