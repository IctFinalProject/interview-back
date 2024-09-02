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

import com.ictedu.adminpage.model.CompanyNoticeModel;
import com.ictedu.adminpage.service.CompanyNoticeService;
import com.ictedu.user.model.entity.User;
import com.ictedu.user.repository.UserRepository;

@RestController
@RequestMapping("/api/companynotice")
public class CompanyNoticeController {
	
	@Autowired
	private CompanyNoticeService companyNoticeService;
	
	@Autowired
	private UserRepository userRepository;
	
	@GetMapping
	public List<CompanyNoticeModel> getAllCompanyNotice(){
		List<CompanyNoticeModel> companyNoticeList = companyNoticeService.getAllCompanyNotice();
		companyNoticeList.forEach(companynotice -> {
		});
		return companyNoticeList;
	}

	@GetMapping("/{companyNoticeId}")
	public ResponseEntity<CompanyNoticeModel> getCompanyNoticeById(@PathVariable String companyNoticeId) {
		try {
			// String 타입의 NoticeId를 long 타입으로 변환
	        long id = Long.parseLong(companyNoticeId);
	        return companyNoticeService.getCompanyNoticeById(id)
	                .map(ResponseEntity::ok)
	                .orElse(ResponseEntity.notFound().build());
	    } catch (NumberFormatException e) {
	        // qnaId가 숫자로 변환될 수 없을 때 예외 처리
	        return ResponseEntity.badRequest().build();
	    }
	}
	@PostMapping
	public CompanyNoticeModel createCompanyNotice(@RequestBody Map<String,Object> companyNoticeRequest) {
		String companyNoticeTitle = (String) companyNoticeRequest.get("companyNoticeTitle");
		String companyNoticeContent = (String) companyNoticeRequest.get("companyNoticeContent");
		Long userId = Long.parseLong(companyNoticeRequest.get("id").toString());
		
		//사용자 엔티티를 데이터베이스에서 조회
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found"));
		
		//객체 생성하고 값을 설정
		CompanyNoticeModel companyNoticeModel = new CompanyNoticeModel();
		companyNoticeModel.setCompanyNoticeTitle(companyNoticeTitle);
		companyNoticeModel.setCompanyNoticeContent(companyNoticeContent);
		companyNoticeModel.setUser(user);
		
		//객체를 서비스로 넘겨서 저장
		return companyNoticeService.createdCompanyNotice(companyNoticeModel);
		}
	
	@PutMapping("/{companyNoticeId}")
	public ResponseEntity<CompanyNoticeModel> updateCompanyNotice(
			@PathVariable Long companyNoticeId, 
			@RequestBody Map<String,Object> companyNoticeRequest){
		// 필드 추출
	    String companyNoticeTitle = (String) companyNoticeRequest.get("companyNoticeTitle");
	    String companyNoticeContent = (String) companyNoticeRequest.get("companyNoticeContent");
	    
	    // NoticeModel 가져오기
	    CompanyNoticeModel existingCompanyNotice = companyNoticeService.getCompanyNoticeById(companyNoticeId)
	        .orElse(null);
	    if (existingCompanyNotice == null) {
	        return ResponseEntity.notFound().build();
	    }
	    
	    // 업데이트 필드 설정
	    existingCompanyNotice.setCompanyNoticeTitle(companyNoticeTitle);
	    existingCompanyNotice.setCompanyNoticeContent(companyNoticeContent);

	    // 업데이트된 객체 저장
	    CompanyNoticeModel updatedCompanyNotice = companyNoticeService.updateCompanyNotice(companyNoticeId, existingCompanyNotice);

	    return ResponseEntity.ok(updatedCompanyNotice);
	}
	
	@DeleteMapping("/{companyNoticeId}")
	public ResponseEntity<Void> deleteCompanyNotice(@PathVariable Long companyNoticeId){
		companyNoticeService.deleteCompanyNotice(companyNoticeId);
		return ResponseEntity.noContent().build();
	}
}
