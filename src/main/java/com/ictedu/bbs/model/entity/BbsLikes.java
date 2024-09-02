package com.ictedu.bbs.model.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

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

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "BBS_Likes") //테이블 이름(게시글 좋아요)
public class BbsLikes {

	@Id
	@SequenceGenerator(name="seq_bbs_likes_id",sequenceName = "seq_bbs_likes_id",allocationSize = 1,initialValue = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "seq_bbs_likes_id")
	@Column(name = "bbs_id", nullable = false)
	private Long id;

	@Column(name = "user_id", nullable = false)
	private String user_id;

	@Column(name = "created_time", nullable = false, updatable = false)
	@CreationTimestamp
	private LocalDate created_time;

	// Getter와 Setter 메소드
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}


	public LocalDate getCreated_time() {
		return created_time;
	}

	public void setCreated_time(LocalDate created_time) {
		this.created_time = created_time;
	}

	
}