package com.ictedu.adminpage.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ictedu.adminpage.model.FaqModel;

@Repository
//JPA를 사용하여 DB와 상호작용하는 인터페이스
public interface FaqRepository extends JpaRepository<FaqModel, Long> {
	
	//카테고리별로 FAQ를 찾는 메소드
	List<FaqModel> findByFaqCategory(String faqCategory);
	

}
