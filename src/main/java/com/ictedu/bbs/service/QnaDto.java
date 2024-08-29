package com.ictedu.bbs.service;

import java.time.LocalDateTime;

import com.ictedu.bbs.model.entity.QnA;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QnaDto {
	//엔터티의 필드와 일치하지 않아도 무방 즉 필요한 필드만으로 구성
	private Long qnaId;
	private Long user_id;
	private String title;
	private String question;
	private String answer;
	private LocalDateTime created_time;
	private LocalDateTime edited_time;
	private String status;
	
	//DTO를 ENTITY로 변환하는 메소드
	public QnA toEntity() {
		return QnA.builder()
				.qnaId(qnaId)
				.userId(user_id)
				.title(title)
				.question(question)
				.answer(answer)
				.created_time(created_time)
				.edited_time(edited_time)
				.status(status)
								.build();
	}
	//ENTITY를 DTO로 변환하는 메소드
	public static QnaDto toDto(QnA qna) {
		return QnaDto.builder()
				.qnaId(qna.getQnaId())
				.user_id(qna.getUser_id())
				.title(qna.getTitle())
				.question(qna.getQuestion())
				.answer(qna.getAnswer())
				.created_time(qna.getCreated_time())
				.edited_time(qna.getEdited_time())
				.status(qna.getStatus())
				.build();
	}
	public QnaDto(String question, String answer) {
		this.question = question;
		this.answer = answer;
	}

}
