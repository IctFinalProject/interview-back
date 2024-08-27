package com.ictedu.adminpage.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.ictedu.adminpage.model.QnaModel;
import com.ictedu.adminpage.repository.QnaRepository;
import com.ictedu.user.model.entity.User;
import com.ictedu.user.repository.UserRepository;

@Service
public class QnaService {

    @Autowired
    private QnaRepository qnaRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    // 모든 QnA 목록을 가져오는 메서드
    public List<QnaModel> getAllQna() {
        return qnaRepository.findAll();
    }
    
    // ID로 특정 QnA를 가져오는 메서드
    public Optional<QnaModel> getQnaById(Long id) {
        return qnaRepository.findById(id);
    }
    
    // QnA를 생성하는 메서드
    public QnaModel createQna(QnaModel qnaModel) {

        // 생성 시간 설정 (기존 값이 없다면)
        if (qnaModel.getQnaCreatedTime() == null) {
            qnaModel.setQnaCreatedTime(LocalDate.now());
        }

        // QnA 엔티티를 DB에 저장
        return qnaRepository.save(qnaModel);
    }
    
    // QnA를 삭제하는 메서드
    public void deleteQna(Long id) {
        qnaRepository.deleteById(id);
    } 
    
    // QnA를 업데이트하는 메서드
    public QnaModel updateQna(Long id, String answer, String status) {
        // 주어진 ID로 QnA 엔티티를 조회
        Optional<QnaModel> qnaOpt = qnaRepository.findById(id);
        
        // 만약 해당 QnA가 존재한다면, 업데이트 후 저장
        if (qnaOpt.isPresent()) {
            QnaModel qnaModel = qnaOpt.get();
            qnaModel.setQnaAnswer(answer);
            qnaModel.setQnaStatus(status);
            qnaModel.setQnaEditedTime(LocalDate.now()); // 수정 시간 업데이트
            return qnaRepository.save(qnaModel);
        }
        // 존재하지 않으면 null 반환
        return null;
    }
}