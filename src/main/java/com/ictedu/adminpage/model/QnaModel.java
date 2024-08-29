package com.ictedu.adminpage.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ictedu.user.model.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "qna")
public class QnaModel {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "qna_id_seq")
	@SequenceGenerator(name = "qna_id_seq",sequenceName =" qna_id_seq", allocationSize = 1)
	@Column(name ="qna_id")
	private Long qnaId; //문의 ID

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	@JsonIgnore
	private User user;

	@Column(name = "qna_category", nullable = false, length = 50)
	private String qnaCategory;  // 문의 카테고리 (String으로 처리)


	@Column(name ="qna_title", nullable = false, length =255)
	private String qnaTitle;

	@Column(name ="qna_question", nullable = false, length = 255)
	private String qnaQuestion; //문의 내용

	@Column(name = "qna_answer", nullable = true, length = 255)
	private String qnaAnswer;  // 답변 내용

	@Column(name = "qna_created_time",nullable = false)
	private LocalDateTime qnaCreatedTime = LocalDateTime.now(); //생성시간

	@Column(name = "qna_edited_time")
	private LocalDate qnaEditedTime; //수정시간

	@Column(name = "qna_status", nullable = false, length = 1)
	private String qnaStatus = "N";  // 상태 ('N' - New, 'T' - In Progress, 'P' - Processed)

}
