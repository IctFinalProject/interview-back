package com.ictedu.interview.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ictedu.interview.model.entity.Question;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long>{
	

}
