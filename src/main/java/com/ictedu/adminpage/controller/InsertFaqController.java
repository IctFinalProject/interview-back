package com.ictedu.adminpage.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ictedu.adminpage.model.FaqModel;
import com.ictedu.adminpage.service.InsertFaqService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/faq")  // API 경로를 의미
@Slf4j
public class InsertFaqController {
	
	@Autowired
	private InsertFaqService insertFaqService;
	
	// 모든 FaqModel을 조회 (Select)
	@GetMapping("/all")
	public List<FaqModel> selectAllFaqs() {
		return insertFaqService.selectAllFaqs();
	}
	
	// 특정 카테고리에 속한 FaqModel을 조회 (Select)
	@GetMapping("/category/{faqCategory}")
	public List<FaqModel> selectFaqsByCategory(@PathVariable String faqCategory){
		return insertFaqService.selectFaqsByCategory(faqCategory);
	}
	
	// FaqModel을 데이터베이스에 저장 (Insert)
	@PostMapping
	public FaqModel insertFaq(@RequestBody FaqModel faqModel) {
		return insertFaqService.insertFaq(faqModel);
	}
	
	// FaqModel을 수정 (Update)
	@PutMapping("/update")
	public FaqModel updateFaq(@RequestBody FaqModel faqModel) {
		return insertFaqService.updateFaq(faqModel);
	}

	// FaqModel을 삭제 (Delete)
	@DeleteMapping("/delete/{faqId}")
	public void deleteFaq(@PathVariable Long faqId) {
		insertFaqService.deleteFaq(faqId);
	}
	
	@GetMapping("/categories")
	public List<String> getFaqCategories() {
		return insertFaqService.getFaqCategories();//서비스에서 카테고리 목록을 반환
	}
}