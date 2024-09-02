package com.ictedu.adminpage.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ictedu.adminpage.model.NoticeModel;
import com.ictedu.adminpage.repository.NoticeRepository;
import com.ictedu.user.repository.UserRepository;

@Service
public class NoticeService {

	@Autowired
	private NoticeRepository noticeRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	  // 
	public List<NoticeModel> getAllNotice() {
		return noticeRepository.findAll();
	}
	
	 // ID로 특정가져오는 메서드
	public Optional<NoticeModel> getNoticeById(Long noticeId){
		return noticeRepository.findById(noticeId);
	}
	
	//Notice를 생성하는 메서드
	public NoticeModel createdNotice(NoticeModel noticeModel) {
		//생성 시간 설정
		if (noticeModel.getNoticeCreatedTime() == null) {
			noticeModel.setNoticeCreatedTime(null);
		}
		return noticeRepository.save(noticeModel);
	}
	//삭제하는 메서드
	public void deleteNotice(Long noticeId) {
		noticeRepository.deleteById(noticeId);
	}
	
	//업데이트 하는 메서드
	public NoticeModel updateNotice(Long noticeId,NoticeModel updatedNoticeData) {
		//Id로 엔티티 조회
		Optional<NoticeModel> noticeOpt = noticeRepository.findById(noticeId);
		
		//만약 존재한다면, 업데이트 후 저장
		if (noticeOpt.isPresent()) {
			NoticeModel noticeModel = noticeOpt.get();
			
			noticeModel.setNoticeTitle(updatedNoticeData.getNoticeTitle());
			noticeModel.setNoticeContent(updatedNoticeData.getNoticeContent());
			noticeModel.setNoticeEditedTime(LocalDateTime.now());
			return noticeRepository.save(noticeModel);
		}
		return null;
	}
}
