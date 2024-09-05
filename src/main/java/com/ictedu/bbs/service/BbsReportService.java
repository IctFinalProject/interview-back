package com.ictedu.bbs.service;

import com.ictedu.bbs.model.entity.Bbs;
import com.ictedu.bbs.model.entity.BbsReport;
import com.ictedu.bbs.repository.BbsReportRepository;
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

    public void saveReport(Bbs bbs, String reason, Map<String, Boolean> additionalInfo) {
        // 추가 정보를 처리하여 문자열로 변환 (예: Map 데이터를 문자열로 변환)
        String additionalInfoString = convertAdditionalInfoToString(additionalInfo);

        BbsReport report = BbsReport.builder()
                .bbs(bbs)  // Bbs 객체와 연결
                .reason(reason)
                .additionalInfo(additionalInfoString)  // 변환된 추가 정보 문자열로 저장
                .reportedAt(LocalDateTime.now())  // 신고된 시간 저장
                .status("Pending")  // 신고 상태 기본값 설정
                .build();

        bbsReportRepository.save(report);
    }

    // Map<String, Boolean>을 문자열로 변환하는 메소드
    private String convertAdditionalInfoToString(Map<String, Boolean> additionalInfo) {
        StringBuilder additionalInfoBuilder = new StringBuilder();
        for (Map.Entry<String, Boolean> entry : additionalInfo.entrySet()) {
            if (entry.getValue()) {  // true인 항목만 추가
                additionalInfoBuilder.append(entry.getKey()).append(", ");
            }
        }
        // 마지막 쉼표 제거
        if (additionalInfoBuilder.length() > 0) {
            additionalInfoBuilder.setLength(additionalInfoBuilder.length() - 2);
        }
        return additionalInfoBuilder.toString();
    }
}
