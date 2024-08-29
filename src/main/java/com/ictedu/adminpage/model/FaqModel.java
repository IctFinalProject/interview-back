package com.ictedu.adminpage.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

//Faq 엔터티 클래스
@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "qna_faq")
public class FaqModel {
	
	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "faq_id_seq")
    @SequenceGenerator(name = "faq_id_seq", sequenceName = "faq_id_seq", allocationSize = 1)
	@Column(name = "faq_id")
    private Long faqId;
	
	@Column(name = "faq_question" , nullable = false )
	private String faqQuestion;
	
	@Column(name = "faq_answer", nullable = true)
	private String faqAnswer;
	
	@Column(name = "faq_category", nullable = false)
	private String faqCategory;
	
	@CreationTimestamp
	@Column(name = "faq_created_time", nullable = false, updatable = false)
	private LocalDateTime faqCreatedTime;
	
	@UpdateTimestamp
	@Column(name = "faq_edited_time")
	private LocalDateTime faqEditedTime;
	
	
}
	
	
