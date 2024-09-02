package com.ictedu.adminpage.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ictedu.adminpage.model.CompanyNoticeModel;
import com.ictedu.adminpage.repository.CompanyNoticeRepository;
import com.ictedu.user.repository.UserRepository;

@Service
public class CompanyNoticeService {

	@Autowired
	private CompanyNoticeRepository companyNoticeRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	  // 
	public List<CompanyNoticeModel> getAllCompanyNotice() {
		return companyNoticeRepository.findAll();
	}
	
	 // 
	public Optional<CompanyNoticeModel> getCompanyNoticeById(Long companyNoticeId){
		return companyNoticeRepository.findById(companyNoticeId);
	}
	
	//CompanyNotice를 생성하는 메서드
	public CompanyNoticeModel createdCompanyNotice(CompanyNoticeModel companyNoticeModel) {
		//생성 시간 설정
		if (companyNoticeModel.getCompanyNoticeCreatedTime() == null) {
			companyNoticeModel.setCompanyNoticeCreatedTime(null);
		}
		return companyNoticeRepository.save(companyNoticeModel);
	}
	//삭제하는 메서드
	public void deleteCompanyNotice(Long companyNoticeId) {
		companyNoticeRepository.deleteById(companyNoticeId);
	}
	
	//업데이트 하는 메서드
	public CompanyNoticeModel updateCompanyNotice(Long companyNoticeId,CompanyNoticeModel updatedCompanyNoticeData) {
		//Id로 엔티티 조회
		Optional<CompanyNoticeModel> companyNoticeOpt = companyNoticeRepository.findById(companyNoticeId);
		
		//만약 존재한다면, 업데이트 후 저장
		if (companyNoticeOpt.isPresent()) {
			CompanyNoticeModel companyNoticeModel = companyNoticeOpt.get();
			
			companyNoticeModel.setCompanyNoticeTitle(updatedCompanyNoticeData.getCompanyNoticeTitle());
			companyNoticeModel.setCompanyNoticeContent(updatedCompanyNoticeData.getCompanyNoticeContent());
			companyNoticeModel.setCompanyNoticeEditedTime(LocalDateTime.now());
			return companyNoticeRepository.save(companyNoticeModel);
		}
		return null;
	}
}
