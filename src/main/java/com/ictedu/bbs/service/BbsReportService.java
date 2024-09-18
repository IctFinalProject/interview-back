package com.ictedu.bbs.service;

import com.ictedu.bbs.model.entity.Bbs;
import com.ictedu.bbs.model.entity.BbsComment;
import com.ictedu.bbs.model.entity.BbsReport;
import com.ictedu.bbs.repository.BbsReportRepository;
import com.ictedu.user.model.entity.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class BbsReportService {

    private final BbsReportRepository bbsReportRepository;

    @Autowired
    public BbsReportService(BbsReportRepository bbsReportRepository) {
        this.bbsReportRepository = bbsReportRepository;
    }

    // 댓글 신고 처리 메서드
    public void saveCommentReport(Bbs bbs, BbsComment comment, User reporter, String reason, Map<String, Boolean> additionalInfo) {
        String additionalInfoString = convertAdditionalInfoToString(additionalInfo);

        BbsReport report = BbsReport.builder()
                .bbs(bbs)
                .comment(comment)
                .reporter(reporter)
                .reason(reason)
                .additionalInfo(additionalInfoString)
                .reportedAt(LocalDateTime.now())
                .status("PENDING")  // 신고 생성 시 기본 상태
                .build();

        bbsReportRepository.save(report);
    }

    // 게시물 신고 처리 메서드
    public void saveReport(Bbs bbs, User reporter, String reason, Map<String, Boolean> additionalInfo) {
        String additionalInfoString = convertAdditionalInfoToString(additionalInfo);

        BbsReport report = BbsReport.builder()
                .bbs(bbs)
                .reporter(reporter)
                .reason(reason)
                .additionalInfo(additionalInfoString)
                .reportedAt(LocalDateTime.now())
                .status("PENDING")
                .build();

        bbsReportRepository.save(report);
    }

    // Map<String, Boolean>을 문자열로 변환하는 메소드
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

