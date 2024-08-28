package com.ictedu.bbs.model.entity;
import java.time.LocalDate;


import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import lombok.NoArgsConstructor;


@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "QnA") //테이블 이름(게시글)

@Builder
public class QnA {

	@Id
	@SequenceGenerator(name="seq_bbs",sequenceName = "seq_bbs",allocationSize = 1,initialValue 	= 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "seq_bbs")
	@Column(name = "qna_id", nullable = false)
	private Long qnaId;

	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Column(name = "title", nullable = false, length = 255)
	private String title;
	
	@Column(name = "question", nullable = false, length = 255)
	private String question;

	@Column(name = "answer", nullable = false, length = 2)
	private String answer;

	@ColumnDefault("SYSDATE")
	@Column(name = "created_time", nullable = false, updatable = false)
	@CreationTimestamp
	private LocalDateTime created_time;

	@Column(name = "edited_time")
	private LocalDateTime edited_time;

	@Column(name = "status", nullable = false, length = 1)
	private String status;

	
	// Getter와 Setter 메소드
	public Long getQna_id() {
		return qnaId;
	}

	public void setQna_id(Long qna_id) {
		this.qnaId = qna_id;
	}

	public Long getUser_id() {
		return userId;
	}

	public void setUser_id(Long user_id) {
		this.userId = user_id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public LocalDateTime getCreated_time() {
		return created_time;
	}

	public void setCreated_time(LocalDateTime created_time) {
		this.created_time = created_time;
	}

	
	public LocalDateTime getEdited_time() {
		return edited_time;
	}

	public void setEdited_time(LocalDateTime edited_time) {
		this.edited_time = edited_time;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}


}
