package com.ictedu.bbs.model.entity;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;


import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
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
@Table(name = "BBS_Comments") //테이블 이름(게시글 댓글)
public class BbsComments {

	@Id
	@SequenceGenerator(name="seq_bbs_comments_id",sequenceName = "seq_bbs_comments_id",allocationSize = 1,initialValue = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "seq_bbs_comments_id")
	@Column(name = "comments_id", nullable = false)
	private Long id;

	@Column(name = "bbs_id", nullable = false)
	private Long bbs_id;

	@Column(name = "comments", nullable = false, length = 255)
	private String comments;

	@Column(name = "created_time", nullable = false, updatable = false)
	@CreationTimestamp
	private LocalDate created_time;
	
	@Column(name = "deleted_time", nullable = true)
	private LocalDateTime deleted_time;
	
	@Column(name = "isDeleted", nullable = false)
	@ColumnDefault("0")
	private Integer isDeleted = 0;
	
	@Column(name = "edited_time", nullable = true)
	private LocalDateTime edited_time;
	
	@Column(name = "isEdited", nullable = false)
	private Integer isEdited = 0;
	
	
	// Getter와 Setter 메소드
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getBbs_id() {
		return bbs_id;
	}

	public void setBbs_id(Long bbs_id) {
		this.bbs_id = bbs_id;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public LocalDate getCreated_time() {
		return created_time;
	}

	public void setCreated_time(LocalDate created_time) {
		this.created_time = created_time;
	}

	public LocalDateTime getDeleted_time() {
		return deleted_time;
	}

	public void setDeleted_time(LocalDateTime deleted_time) {
		this.deleted_time = deleted_time;
	}

	public Integer getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(Integer isDeleted) {
		this.isDeleted = isDeleted;
	}

	public LocalDateTime getEdited_time() {
		return edited_time;
	}

	public void setEdited_time(LocalDateTime edited_time) {
		this.edited_time = edited_time;
	}

	public Integer getIsEdited() {
		return isEdited;
	}

	public void setIsEdited(Integer isEdited) {
		this.isEdited = isEdited;
	}

	

}
