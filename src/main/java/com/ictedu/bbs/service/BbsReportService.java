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

    // BbsReportRepository 주입
    private final BbsReportRepository bbsReportRepository;

    @Autowired
    public BbsReportService(BbsReportRepository bbsReportRepository) {
        this.bbsReportRepository = bbsReportRepository;
    }

    // 댓글 신고 처리 메서드
    public void saveCommentReport(Bbs bbs, BbsComment comment, User reporter, String reason, Map<String, Boolean> additionalInfo) {
        String additionalInfoString = convertAdditionalInfoToString(additionalInfo);

        BbsReport report = BbsReport.builder()
                .bbs(bbs)  // 댓글이 속한 게시물 설정
                .comment(comment)  // 댓글 설정
                .reporter(reporter)  // 신고자 정보 추가
                .reason(reason)
                .additionalInfo(additionalInfoString)
                .reportedAt(LocalDateTime.now())
                .status(BbsReport.Status.PENDING)  // Enum 상수 사용
                .build();

        bbsReportRepository.save(report);  // 주입된 repository 인스턴스를 통해 save 호출
    }

    // 게시물 신고 처리 메서드 (추가)
    public void saveReport(Bbs bbs, User reporter, String reason, Map<String, Boolean> additionalInfo) {
        String additionalInfoString = convertAdditionalInfoToString(additionalInfo);

        BbsReport report = BbsReport.builder()
                .bbs(bbs)  // 게시물 설정
                .reporter(reporter)  // 신고자 정보 추가
                .reason(reason)
                .additionalInfo(additionalInfoString)
                .reportedAt(LocalDateTime.now())
                .status(BbsReport.Status.PENDING)  // Enum 상수 사용
                .build();

        bbsReportRepository.save(report);  // 주입된 repository 인스턴스를 통해 save 호출
    }

    // Map<String, Boolean>을 문자열로 변환하는 메소드
    private String convertAdditionalInfoToString(Map<String, Boolean> additionalInfo) {
        StringBuilder additionalInfoBuilder = new StringBuilder();
        for (Map.Entry<String, Boolean> entry : additionalInfo.entrySet()) {
            if (entry.getValue()) {  // true인 항목만 추가
                additionalInfoBuilder.append(entry.getKey()).append(", ");
            }
        }
        if (additionalInfoBuilder.length() > 0) {
            additionalInfoBuilder.setLength(additionalInfoBuilder.length() - 2);
        }
        return additionalInfoBuilder.toString();
    }
}
