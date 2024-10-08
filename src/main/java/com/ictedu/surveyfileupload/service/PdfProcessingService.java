package com.ictedu.surveyfileupload.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class PdfProcessingService {

    public List<String> extractInfoFromPDF(byte[] fileData) throws IOException {
        List<String> extractedInfo = new ArrayList<>();

        String[] sections = {"중졸이하", "고졸", "대졸", "대학원졸", "계열무관", "인문", "사회", "교육", "공학", "자연", "의학", "예체능"};

        try (PDDocument document = PDDocument.load(fileData)) {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setStartPage(3);  
            stripper.setEndPage(3);    
            String pageText = stripper.getText(document);

            for (String line : pageText.split("\n")) {
                line = line.trim();

                // 섹션 이름은 콘솔 출력에서 제외
                if (isSectionName(line, sections)) {
                    continue; // 섹션 이름은 무시하고 다음 라인으로
                }


                if (!line.isEmpty()) {
                    // 줄의 모든 직업을 쉼표로 구분하여 개별적으로 처리
                    String[] jobs = line.split(",");
                    for (String job : jobs) {
                        job = job.trim();
                        addJobToList(extractedInfo, job);
                    }
                }
            }
        }

        // 추출된 내용이 없는 경우 메시지 추가
        if (extractedInfo.isEmpty()) {
            extractedInfo.add("추출할 내용이 없습니다");
        }
        return extractedInfo;
    }

    private boolean isSectionName(String line, String[] sections) {
        for (String section : sections) {
            // 섹션 이름이 정확히 일치하는지 확인
            if (line.equals(section)) {
                return true;
            }
        }
        return false;
    }

    private void addJobToList(List<String> list, String job) {
        job = job.trim();  // 공백 제거
        if (!job.isEmpty() && !list.contains(job)) {  // 중복 확인 후 추가
            list.add(job);
        }
    }
}
