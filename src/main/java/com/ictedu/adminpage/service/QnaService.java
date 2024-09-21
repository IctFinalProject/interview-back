package com.ictedu.adminpage.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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

    // 특정 QnA를 ID와 비밀번호로 가져오는 메서드
    public Optional<QnaModel> getQnaByIdAndPassword(Long qnaId, String inputPassword, Long userId) {
        // ID로 QnA를 찾는다
        Optional<QnaModel> qnaOpt = qnaRepository.findById(qnaId);
        if (qnaOpt.isPresent()) {
            QnaModel qna = qnaOpt.get();
            Optional<User> userOpt = userRepository.findById(userId);
            User user = userOpt.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found with id: " + userId));
            
            // 관리자는 비밀번호 없이 모든 글을 볼 수 있음
            if (user.getIsAdmin()) {
                return qnaOpt;  // 관리자이면 비밀번호 확인 없이 반환
            }
            
            // 사용자가 입력한 비밀번호와 DB에 저장된 비밀번호가 일치하는지 확인
            if (qna.getQnaPassword().equals(inputPassword)) {
                return qnaOpt;  // 비밀번호가 맞으면 반환
            } else {
            	//비밀번호가 틀렸을 때 Optional.empty()를 반환
                return Optional.empty();
            }
        }
        return Optional.empty(); // QnA가 없으면 빈 값 반환
    }

    // ID값을 인자로 받아 조회
    public Optional<QnaModel> getQnaById(Long qnaId) {
        return qnaRepository.findById(qnaId);
    }
    
    // QnA를 생성하는 메서드
    public QnaModel createQna(QnaModel qnaModel) {
        if (qnaModel.getQnaCreatedTime() == null) {
            qnaModel.setQnaCreatedTime(null);
        }
        return qnaRepository.save(qnaModel);  // QnA 엔티티를 DB에 저장
    }

    // QnA를 삭제하는 메서드
    public void deleteQna(Long qnaId) {
        qnaRepository.deleteById(qnaId);
    }

    // QnA를 업데이트하는 메서드
    public Optional<QnaModel> updateQna(Long qnaId, QnaModel updatedQnaData) {
        Optional<QnaModel> qnaOpt = qnaRepository.findById(qnaId);

        if (qnaOpt.isPresent()) {
            QnaModel qnaModel = qnaOpt.get();
            qnaModel.setQnaAnswer(updatedQnaData.getQnaAnswer());
            qnaModel.setQnaStatus(updatedQnaData.getQnaStatus());
            qnaModel.setQnaEditedTime(LocalDate.now()); // 수정 시간 업데이트
            return Optional.of(qnaRepository.save(qnaModel));
        }
        return Optional.empty();
    }
}
