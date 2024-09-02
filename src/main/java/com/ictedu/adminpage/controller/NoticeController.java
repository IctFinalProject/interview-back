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

import com.ictedu.adminpage.model.NoticeModel;
import com.ictedu.adminpage.service.NoticeService;
import com.ictedu.user.model.entity.User;
import com.ictedu.user.repository.UserRepository;

@RestController
@RequestMapping("/api/notice")
public class NoticeController {
	
	@Autowired
	private NoticeService noticeService;
	
	@Autowired
	private UserRepository userRepository;
	
	@GetMapping
	public List<NoticeModel> getAllNotice(){
		List<NoticeModel> noticeList = noticeService.getAllNotice();
		noticeList.forEach(notice -> {
		});
		return noticeList;
	}

	@GetMapping("/{noticeId}")
	public ResponseEntity<NoticeModel> getNoticeById(@PathVariable String noticeId) {
		try {
			// String 타입의 adminNoticeId를 long 타입으로 변환
	        long id = Long.parseLong(noticeId);
	        return noticeService.getNoticeById(id)
	                .map(ResponseEntity::ok)
	                .orElse(ResponseEntity.notFound().build());
	    } catch (NumberFormatException e) {
	        // qnaId가 숫자로 변환될 수 없을 때 예외 처리
	        return ResponseEntity.badRequest().build();
	    }
	}
	@PostMapping
	public NoticeModel createNotice(@RequestBody Map<String,Object> noticeRequest) {
		String noticeTitle = (String) noticeRequest.get("noticeTitle");
		String noticeContent = (String) noticeRequest.get("noticeContent");
		Long userId = Long.parseLong(noticeRequest.get("id").toString());
		
		//사용자 엔티티를 데이터베이스에서 조회
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found"));
		
		//객체 생성하고 값을 설정
		NoticeModel noticeModel = new NoticeModel();
		noticeModel.setNoticeTitle(noticeTitle);
		noticeModel.setNoticeContent(noticeContent);
		noticeModel.setUser(user);
		
		//객체를 서비스로 넘겨서 저장
		return noticeService.createdNotice(noticeModel);
		}
	
	@PutMapping("/{noticeId}")
	public ResponseEntity<NoticeModel> updateNotice(
			@PathVariable Long noticeId, 
			@RequestBody Map<String,Object> noticeRequest){
		// 필드 추출
	    String noticeTitle = (String) noticeRequest.get("noticeTitle");
	    String noticeContent = (String) noticeRequest.get("noticeContent");
	    
	    // NoticeModel 가져오기
	    NoticeModel existingNotice = noticeService.getNoticeById(noticeId)
	        .orElse(null);
	    if (existingNotice == null) {
	        return ResponseEntity.notFound().build();
	    }
	    
	    // 업데이트 필드 설정
	    existingNotice.setNoticeTitle(noticeTitle);
	    existingNotice.setNoticeContent(noticeContent);

	    // 업데이트된 객체 저장
	    NoticeModel updatedNotice = noticeService.updateNotice(noticeId, existingNotice);

	    return ResponseEntity.ok(updatedNotice);
	}
	
	@DeleteMapping("/{noticeId}")
	public ResponseEntity<Void> deleteNotice(@PathVariable Long noticeId){
		noticeService.deleteNotice(noticeId);
		return ResponseEntity.noContent().build();
	}
}
