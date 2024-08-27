package com.ictedu.adminpage.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ictedu.adminpage.model.FaqModel;
import com.ictedu.adminpage.repository.FaqRepository;

//비즈니스 로직을 처리하는 서비스 클래스
@Service
public class InsertFaqService {
	
	@Autowired
	private FaqRepository faqRepository; //FaqRepository를 주입받음
	
	//Faq를 데이터베이스에 저장 (Insert)
	public FaqModel insertFaq(FaqModel faqModel) {
		return faqRepository.save(faqModel);
	}
	
	//데이터베이스에서 모든 FaqModel을 조회(Select)
	public List<FaqModel> selectAllFaqs() {
		return faqRepository.findAll();
	}
	//특정 카테고리에 속한 FaqModel을 조회 (Select)
	public List<FaqModel> selectFaqsByCategory(String faqCategory){
		return faqRepository.findByFaqCategory(faqCategory);
	}
	
	//기존 FaqModel을 수정(Update)
	public FaqModel updateFaq(FaqModel faqModel) {
		return faqRepository.save(faqModel);
	}
	
	//삭제
	public void deleteFaq(Long faqId) {
		faqRepository.deleteById(faqId);
	}
	
	//데이터베이스에 저장된 모든 FAQ 항목에서 중복 없이 카테고리 목록을 반환(NEW)
	public List<String> getFaqCategories(){
		return faqRepository.findAll() 
							.stream()
							.map(FaqModel::getFaqCategory)//카테고리 추출
							.distinct()//중복제거
							.collect(Collectors.toList());//리스트로 반환
	}
}
