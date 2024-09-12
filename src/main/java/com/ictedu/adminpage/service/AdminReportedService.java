package com.ictedu.adminpage.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ictedu.adminpage.repository.AdminDeletedCommentRepository;
import com.ictedu.adminpage.repository.AdminReportedCommentRepository;
import com.ictedu.adminpage.repository.AdminReportedPostRepository;
import com.ictedu.bbs.model.entity.BbsComment;
import com.ictedu.bbs.model.entity.BbsReport;
import com.ictedu.bbs.repository.BbsCommentRepository;
import com.ictedu.bbs.repository.BbsRepository;

@Service
public class AdminReportedService {

    private final AdminReportedPostRepository adminReportedPostRepository;
    private final BbsRepository bbsRepository;
    private final AdminDeletedCommentRepository adminDeletedCommentRepository;
    private final AdminReportedCommentRepository reportedCommentRepository;
    private final BbsCommentRepository bbsCommentRepository;  // BbsCommentRepository 필드 추가

    // 생성자 수정 (BbsCommentRepository를 인자로 추가)
    public AdminReportedService(AdminReportedPostRepository adminReportedPostRepository,
                                BbsRepository bbsRepository,
                                AdminDeletedCommentRepository adminDeletedCommentRepository,
                                AdminReportedCommentRepository reportedCommentRepository,
                                BbsCommentRepository bbsCommentRepository) {
        this.adminReportedPostRepository = adminReportedPostRepository;
        this.bbsRepository = bbsRepository;
        this.adminDeletedCommentRepository = adminDeletedCommentRepository;
        this.reportedCommentRepository = reportedCommentRepository;
        this.bbsCommentRepository = bbsCommentRepository;  // 초기화
    }

	// 모든 신고된 게시글을 조회
	public List<BbsReport> getAllReportedPosts() {
		return adminReportedPostRepository.findAll();
	}

	// 신고된 댓글 목록 조회
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

	@Transactional
	public void deleteCommentPermanently(Long reportId) {
		// 1. BbsReport 엔티티를 통해 신고된 댓글 정보 조회
		BbsReport bbsReport = reportedCommentRepository.findById(reportId)
				.orElseThrow(() -> new IllegalArgumentException("해당 신고 번호가 없습니다: " + reportId));

		// 2. 신고된 댓글 정보 가져오기
		BbsComment bbsComment = bbsReport.getComment();

		// 3. 해당 댓글에 대한 신고 기록 삭제 (BbsReport에서 삭제)
		reportedCommentRepository.deleteById(reportId);

		// 4. 댓글 삭제 (BbsComment에서 삭제)
		if (bbsComment != null) {
			bbsCommentRepository.deleteById(bbsComment.getCommentId());
		}
	}


	// 신고된 댓글 복구 (신고 기록 삭제)
	@Transactional
	public void restoreReportedComment(Long reportId) {
		reportedCommentRepository.deleteById(reportId);
	}
}
