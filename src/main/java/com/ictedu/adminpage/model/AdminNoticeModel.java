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
@Table(name = "adminnotice")

public class AdminNoticeModel {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "adminnotice_id_seq")
	@SequenceGenerator(name = "adminnotice_id_seq",sequenceName =" adminnotice_id_seq", allocationSize = 1)
	@Column(name ="adminnotice_id")
	private Long adminNoticeId; //문의 ID
	
	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;
	
	@Column(name = "adminnotice_title", nullable = false, length =255)
	private String adminNoticeTitle; //제목
	
	@Column(name = "adminnotice_content",nullable = true, length = 255 )
	private String adminNoticeContent; //내용
	
	@Column(name = "adminnotice_created_time" , nullable = false)
	private LocalDateTime adminNoticeCreatedTime = LocalDateTime.now(); //생성시간
	
	@Column(name = "adminnotice_edited_time")
	private LocalDateTime adminNoticeEditedTime = LocalDateTime.now(); //수정시간
	
}
