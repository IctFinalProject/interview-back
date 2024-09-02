package com.ictedu.adminpage.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ictedu.adminpage.model.AdminNoticeModel;
import com.ictedu.adminpage.repository.AdminNoticeRepository;
import com.ictedu.user.repository.UserRepository;

@Service
public class AdminNoticeService {

	@Autowired
	private AdminNoticeRepository adminNoticeRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	  // 모든 QnA 목록을 가져오는 메서드
	public List<AdminNoticeModel> getAllAdminNotice() {
		return adminNoticeRepository.findAll();
	}
	
	 // ID로 특정 QnA를 가져오는 메서드
	public Optional<AdminNoticeModel> getAdminNoticeById(Long adminNoticeId){
		return adminNoticeRepository.findById(adminNoticeId);
	}
	
	//AdminNotice를 생성하는 메서드
	public AdminNoticeModel createdAdminNotice(AdminNoticeModel adminNoticeModel) {
		//생성 시간 설정
		if (adminNoticeModel.getAdminNoticeCreatedTime() == null) {
			adminNoticeModel.setAdminNoticeCreatedTime(null);
		}
		return adminNoticeRepository.save(adminNoticeModel);
	}
	//삭제하는 메서드
	public void deleteAdminNotice(Long adminNoticeId) {
		adminNoticeRepository.deleteById(adminNoticeId);
	}
	
	//업데이트 하는 메서드
	public AdminNoticeModel updateAdminNotice(Long adminNoticeId,AdminNoticeModel updatedAdminNoticeData) {
		//Id로 엔티티 조회
		Optional<AdminNoticeModel> adminNoticeOpt = adminNoticeRepository.findById(adminNoticeId);
		
		//만약 존재한다면, 업데이트 후 저장
		if (adminNoticeOpt.isPresent()) {
			AdminNoticeModel adminNoticeModel = adminNoticeOpt.get();
			
			adminNoticeModel.setAdminNoticeTitle(updatedAdminNoticeData.getAdminNoticeTitle());
			adminNoticeModel.setAdminNoticeContent(updatedAdminNoticeData.getAdminNoticeContent());
			adminNoticeModel.setAdminNoticeEditedTime(LocalDateTime.now());
			return adminNoticeRepository.save(adminNoticeModel);
		}
		return null;
	}
}
