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

import com.ictedu.adminpage.model.AdminNoticeModel;
import com.ictedu.adminpage.service.AdminNoticeService;
import com.ictedu.user.model.entity.User;
import com.ictedu.user.repository.UserRepository;

@RestController
@RequestMapping("/api/adminnotice")
public class AdminNoticeController {
	
	@Autowired
	private AdminNoticeService adminNoticeService;
	
	@Autowired
	private UserRepository userRepository;
	
	@GetMapping
	public List<AdminNoticeModel> getAllAdminNotice(){
		List<AdminNoticeModel> adminNoticeList = adminNoticeService.getAllAdminNotice();
		adminNoticeList.forEach(adminnotice -> {
		});
		return adminNoticeList;
	}

	@GetMapping("/{adminNoticeId}")
	public ResponseEntity<AdminNoticeModel> getAdminNoticeById(@PathVariable String adminNoticeId) {
		try {
			// String 타입의 adminNoticeId를 long 타입으로 변환
	        long id = Long.parseLong(adminNoticeId);
	        return adminNoticeService.getAdminNoticeById(id)
	                .map(ResponseEntity::ok)
	                .orElse(ResponseEntity.notFound().build());
	    } catch (NumberFormatException e) {
	        // qnaId가 숫자로 변환될 수 없을 때 예외 처리
	        return ResponseEntity.badRequest().build();
	    }
	}
	@PostMapping
	public AdminNoticeModel createAdminNotice(@RequestBody Map<String,Object> adminNoticeRequest) {
		String adminNoticeTitle = (String) adminNoticeRequest.get("adminNoticeTitle");
		String adminNoticeContent = (String) adminNoticeRequest.get("adminNoticeContent");
		Long userId = Long.parseLong(adminNoticeRequest.get("id").toString());
		
		//사용자 엔티티를 데이터베이스에서 조회
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found"));
		
		//객체 생성하고 값을 설정
		AdminNoticeModel adminNoticeModel = new AdminNoticeModel();
		adminNoticeModel.setAdminNoticeTitle(adminNoticeTitle);
		adminNoticeModel.setAdminNoticeContent(adminNoticeContent);
		adminNoticeModel.setUser(user);
		
		//객체를 서비스로 넘겨서 저장
		return adminNoticeService.createdAdminNotice(adminNoticeModel);
		}
	
	@PutMapping("/{adminNoticeId}")
	public ResponseEntity<AdminNoticeModel> updateAdminNotice(@PathVariable Long adminNoticeId, @RequestBody AdminNoticeModel adminNoticeModel){
		AdminNoticeModel updatedAdminNotice = adminNoticeService.updateAdminNotice(adminNoticeId, adminNoticeModel);
		return updatedAdminNotice != null ? ResponseEntity.ok(updatedAdminNotice) : ResponseEntity.notFound().build();
	}
	
	@DeleteMapping("/{adminNoticeId}")
	public ResponseEntity<Void> deleteAdminNotice(@PathVariable Long adminNoticeId){
		adminNoticeService.deleteAdminNotice(adminNoticeId);
		return ResponseEntity.noContent().build();
	}
}
