package com.ictedu.adminpage.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ictedu.adminpage.model.QnaModel;
import com.ictedu.adminpage.service.QnaService;
import com.ictedu.user.model.entity.User;
import com.ictedu.user.repository.UserRepository;

@RestController
@RequestMapping("/api/qna")
public class QnaController {
	
	@Autowired
	private QnaService qnaService;
	
    @Autowired
    private UserRepository userRepository;
    
	@GetMapping
	public List<QnaModel> getAllQna(){
		List<QnaModel> qnaList = qnaService.getAllQna();
	    qnaList.forEach(qna -> {
	    });
	    return qnaList;
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<QnaModel> getQnaById(@PathVariable Long id) {
		return qnaService.getQnaById(id)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}
	
	@PostMapping
	public QnaModel createQna(@RequestBody Map<String, Object> qnaRequest) {
	    // Request에서 필요한 데이터를 추출합니다.
	    String category = (String) qnaRequest.get("category");
	    String qnaTitle = (String) qnaRequest.get("qnaTitle");
	    String qnaQuestion = (String) qnaRequest.get("qnaQuestion");
	    Long userId = Long.parseLong(qnaRequest.get("id").toString());

	    // 사용자 엔티티를 데이터베이스에서 조회합니다.
	    User user = userRepository.findById(userId)
	        .orElseThrow(() -> new RuntimeException("User not found"));

	    // QnaModel 객체를 생성하고 값을 설정합니다.
	    QnaModel qnaModel = new QnaModel();
	    qnaModel.setQnaCategory(category);
	    qnaModel.setQnaQuestion(qnaQuestion);
	    qnaModel.setQnaTitle(qnaTitle); //
	    qnaModel.setUser(user);

	    // QnaModel 객체를 서비스로 넘겨서 저장합니다.
	    return qnaService.createQna(qnaModel);
	}
	
	@PutMapping("/{id}")
    public ResponseEntity<QnaModel> updateQna(@PathVariable Long id, @RequestBody QnaModel qnaModel) {
        QnaModel updatedQna = qnaService.updateQna(id);
        return updatedQna != null ? ResponseEntity.ok(updatedQna) : ResponseEntity.notFound().build();
    }
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteQna(@PathVariable Long id) {
		qnaService.deleteQna(id);
		return ResponseEntity.noContent().build();
	}
}
