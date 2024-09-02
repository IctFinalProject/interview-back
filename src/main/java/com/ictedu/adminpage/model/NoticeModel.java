package com.ictedu.adminpage.model;

import java.time.LocalDateTime;

import com.ictedu.user.model.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "notice")

public class NoticeModel {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "notice_id_seq")
	@SequenceGenerator(name = "notice_id_seq",sequenceName =" notice_id_seq", allocationSize = 1)
	@Column(name ="notice_id")
	private Long NoticeId; //문의 ID
	
	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;
	
	@Column(name = "notice_title", nullable = false, length =255)
	private String noticeTitle; //제목
	
	@Column(name = "notice_content",nullable = true, length = 255 )
	private String noticeContent; //내용
	
	@Column(name = "notice_created_time" , nullable = false)
	private LocalDateTime noticeCreatedTime = LocalDateTime.now(); //생성시간
	
	@Column(name = "notice_edited_time")
	private LocalDateTime noticeEditedTime = LocalDateTime.now(); //수정시간
	
}
