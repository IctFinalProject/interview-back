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
@Table(name = "companynotice")

public class CompanyNoticeModel {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "companynotice_id_seq")
	@SequenceGenerator(name = "companynotice_id_seq",sequenceName =" companynotice_id_seq", allocationSize = 1)
	@Column(name ="companynotice_id")
	private Long companyNoticeId; //문의 ID
	
	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;
	
	@Column(name = "companynotice_title", nullable = false, length =255)
	private String companyNoticeTitle; //제목
	
	@Column(name = "companynotice_content",nullable = true, length = 255 )
	private String companyNoticeContent; //내용
	
	@Column(name = "companynotice_created_time" , nullable = false)
	private LocalDateTime companyNoticeCreatedTime = LocalDateTime.now(); //생성시간
	
	@Column(name = "companynotice_edited_time")
	private LocalDateTime companyNoticeEditedTime = LocalDateTime.now(); //수정시간
	
}
