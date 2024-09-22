package com.ictedu.adminpage.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ictedu.adminpage.model.QnaModel;
import com.ictedu.adminpage.service.QnaService;
import com.ictedu.user.model.entity.User;
import com.ictedu.user.repository.UserRepository;
import com.ictedu.user.service.UserService;

@RestController
@RequestMapping("/api/qna")
public class QnaController {

	@Autowired
	private UserService userService;

	@Autowired
	private QnaService qnaService;

	@Autowired
	private UserRepository userRepository;

	// 사용자의 모든 QnA를 가져오는 엔드포인트
	@GetMapping
	public ResponseEntity<List<QnaModel>> getAllQna() {
		//모든 QnA가져오기(관리자만 접근 가능)
		List<QnaModel> qnaList = qnaService.getAllQna();
		return ResponseEntity.ok(qnaList);
	}

	// 비밀번호로 보호된 특정 QnA를 ID로 조회
	@GetMapping("/{qnaId}")
	public ResponseEntity<QnaModel> getQnaById(
			@PathVariable Long qnaId, 
			@RequestParam String password) {

		// QnA 존재 여부 확인
		Optional<QnaModel> qnaOpt = qnaService.getQnaById(qnaId);

		if (!qnaOpt.isPresent()) {
			return ResponseEntity.notFound().build(); // QnA가 없을 때 404 Not Found
		}

		QnaModel qna = qnaOpt.get();

		// 비밀번호 일치 여부 확인
		if (!qna.getQnaPassword().equals(password)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null); // 비밀번호 불일치 시 403 Forbidden
		}

		return ResponseEntity.ok(qna); // 비밀번호가 일치하면 QnA 반환
	}

	// QnA를 생성하는 엔드포인트
	@PostMapping
	public ResponseEntity<QnaModel> createQna(@RequestBody Map<String, Object> qnaRequest) {
		// Request에서 필요한 데이터를 추출
		String category = (String) qnaRequest.getOrDefault("category", "");
		String qnaTitle = (String) qnaRequest.getOrDefault("qnaTitle", "");
		String qnaQuestion = (String) qnaRequest.getOrDefault("qnaQuestion", "");
		String qnaPassword = (String) qnaRequest.getOrDefault("qnaPassword", "");  // 비밀번호도 추가
		String email = (String) qnaRequest.getOrDefault("email", "");

		// 필수 입력값 확인
		if (category.isEmpty() || qnaTitle.isEmpty() || qnaQuestion.isEmpty() || qnaPassword.isEmpty() || email.isEmpty()) {
			return ResponseEntity.badRequest().body(null); //입력값 없으면 400에러
		}

		// 이메일을 통해 사용자 정보 조회
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new RuntimeException("해당 이메일로 사용자를 찾을 수 없습니다."));

		// QnaModel 객체를 생성하고 값을 설정
		QnaModel qnaModel = new QnaModel();
		qnaModel.setQnaCategory(category);
		qnaModel.setQnaQuestion(qnaQuestion);
		qnaModel.setQnaTitle(qnaTitle);
		qnaModel.setQnaPassword(qnaPassword);  // 비밀번호 저장
		qnaModel.setUser(user);  // 인증된 사용자

		// QnaModel 객체를 서비스로 넘겨서 저장
		QnaModel newQna = qnaService.createQna(qnaModel);
		return ResponseEntity.ok(newQna);
	}

	//관리자만 접근 가능한 QnA 상세보기 엔드포인트
	@GetMapping("/details/{qnaId}")
	public ResponseEntity<QnaModel> getQnaDetailsById(@PathVariable Long qnaId) {

		Optional<QnaModel> qnaOpt = qnaService.getQnaById(qnaId);
		if(!qnaOpt.isPresent()) {
			return ResponseEntity.notFound().build(); //QnA가 없을 때 404에러
		}

		QnaModel qna = qnaOpt.get();
		return ResponseEntity.ok(qna);
	}


	// QnA를 업데이트하는 엔드포인트
	@PutMapping("/{qnaId}")
	public ResponseEntity<QnaModel> updateQna(@PathVariable Long qnaId, @RequestBody QnaModel qnaModel) {
		Optional<QnaModel> existingQnaOpt = qnaService.getQnaById(qnaId);

		if (existingQnaOpt.isPresent()) {
			Optional<QnaModel> updatedQnaOpt = qnaService.updateQna(qnaId, qnaModel);
			return updatedQnaOpt.map(ResponseEntity::ok)
					.orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
		} else {

			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}

	// QnA를 삭제하는 엔드포인트
	@DeleteMapping("/{qnaId}")
	public ResponseEntity<Void> deleteQna(@PathVariable Long qnaId, @RequestParam Long userId) {
		// 사용자 정보 조회
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found"));

		// 삭제하려는 QnA가 본인의 것인지 또는 관리자인지 확인
		Optional<QnaModel> existingQnaOpt = qnaService.getQnaById(qnaId);
		if (existingQnaOpt.isPresent()) {
			QnaModel existingQna = existingQnaOpt.get();
			if (existingQna.getUser().getId().equals(user.getId()) || user.getIsAdmin()) {
				qnaService.deleteQna(qnaId);
				return ResponseEntity.noContent().build();
			} else {
				return ResponseEntity.status(403).build(); // 권한이 없을 때 403 Forbidden
			}
		} else {
			return ResponseEntity.notFound().build();
		}
	}
	// QnA 답변을 등록하고 상태를 업데이트하는 엔드포인트
	@PutMapping("/answer/{qnaId}")
	public ResponseEntity<QnaModel> answerQna(
			@PathVariable Long qnaId, 
			@RequestBody Map<String, Object> answerRequest) {

		// QnA 존재 여부 확인
		Optional<QnaModel> qnaOpt = qnaService.getQnaById(qnaId);

		if (!qnaOpt.isPresent()) {
			return ResponseEntity.notFound().build(); // QnA가 없을 때 404 Not Found
		}

		QnaModel qna = qnaOpt.get();

		// Request에서 답변과 상태를 추출
		String answer = (String) answerRequest.getOrDefault("qnaAnswer", "");
		String status = (String) answerRequest.getOrDefault("qnaStatus", "");

		// 답변이 비어있지 않은 경우 QnA의 답변 필드를 업데이트
		if (!answer.isEmpty()) {
			qna.setQnaAnswer(answer);
		}

		// 상태가 제공된 경우 QnA 상태를 업데이트
		if (!status.isEmpty()) {
			qna.setQnaStatus(status);
		}

		// 변경 사항을 저장
		Optional<QnaModel> updatedQnaOpt = qnaService.updateQna(qnaId, qna);

		return updatedQnaOpt.map(ResponseEntity::ok)
				.orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
	}

}