package com.ictedu.bbs.service;

import com.ictedu.bbs.model.entity.Bbs;
import com.ictedu.bbs.model.entity.BbsComment;
import com.ictedu.bbs.model.entity.BbsReport;
import com.ictedu.bbs.repository.BbsReportRepository;
import com.ictedu.bbs.repository.BbsCommentRepository;  // 댓글 저장을 위한 리포지토리 추가
import com.ictedu.bbs.repository.BbsRepository;
import com.ictedu.user.model.entity.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class BbsReportService {

    private final BbsReportRepository bbsReportRepository;
    private final BbsRepository bbsRepository;
    private final BbsCommentRepository bbsCommentRepository;  // 댓글 저장소 추가

    @Autowired
    public BbsReportService(BbsReportRepository bbsReportRepository, BbsRepository bbsRepository, BbsCommentRepository bbsCommentRepository) {
        this.bbsReportRepository = bbsReportRepository;
        this.bbsRepository = bbsRepository;
        this.bbsCommentRepository = bbsCommentRepository;  // 댓글 저장소 초기화
    }

    // 댓글 신고 처리 메서드
    @Transactional
    public void saveCommentReport(Bbs bbs, BbsComment comment, User reporter, String reason, Map<String, Boolean> additionalInfo) {
        String additionalInfoString = convertAdditionalInfoToString(additionalInfo);

        // 새로운 신고 엔티티 생성
        BbsReport report = BbsReport.builder()
                .bbs(bbs)
                .comment(comment)
                .reporter(reporter)
                .reason(reason)
                .additionalInfo(additionalInfoString)
                .reportedAt(LocalDateTime.now())
                .status("PENDING")  // 신고 생성 시 기본 상태
                .build();

        // 신고 저장
        bbsReportRepository.save(report);

        // 댓글의 deletedReason을 1로 설정하고 저장
        updateCommentStatusOnReport(comment);
    }

    // 댓글의 상태를 업데이트하는 메서드
    private void updateCommentStatusOnReport(BbsComment comment) {
        comment.setDeletedReason(1); // 신고로 인한 삭제 이유
        bbsCommentRepository.save(comment); // 댓글 상태 업데이트 후 저장
    }

    // 기존 게시물 신고 처리 메서드 (변경 없음)
    @Transactional
    public void saveReport(Bbs bbs, User reporter, String reason, Map<String, Boolean> additionalInfo) {
        String additionalInfoString = convertAdditionalInfoToString(additionalInfo);

        // 새로운 신고 엔티티 생성
        BbsReport report = BbsReport.builder()
                .bbs(bbs)
                .reporter(reporter)
                .reason(reason)
                .additionalInfo(additionalInfoString)
                .reportedAt(LocalDateTime.now())
                .status("PENDING")
                .build();

        // 신고를 저장
        bbsReportRepository.save(report);

        // 게시글의 deletedReason을 1로 설정하고 저장
        updateBbsStatusOnReport(bbs);
    }

    // 게시글의 상태를 업데이트하는 메서드
    private void updateBbsStatusOnReport(Bbs bbs) {
        bbs.setDeletedReason(1); // 신고로 인한 삭제 이유
        bbs.setStatus("HIDDEN"); // 게시글 상태를 숨김으로 변경
        bbsRepository.save(bbs); // 게시글 상태 업데이트 후 저장
    }

    // Map<String, Boolean>을 문자열로 변환하는 메소드 (기존 메서드)
    private String convertAdditionalInfoToString(Map<String, Boolean> additionalInfo) {
        StringBuilder additionalInfoBuilder = new StringBuilder();
        for (Map.Entry<String, Boolean> entry : additionalInfo.entrySet()) {
            if (entry.getValue()) {
                additionalInfoBuilder.append(entry.getKey()).append(", ");
            }
        }
        if (additionalInfoBuilder.length() > 0) {
            additionalInfoBuilder.setLength(additionalInfoBuilder.length() - 2);
        }
        return additionalInfoBuilder.toString();
    }
}
