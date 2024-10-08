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
@Table(name = "COMMENT_Report") //테이블 이름(댓글 신고)
public class CommentReport {

	@Id
	@SequenceGenerator(name="seq_bbs_comment_id",sequenceName = "seq_bbs_comment_id",allocationSize = 1,initialValue = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "seq_bbs_comment_id")
	@Column(name = "comment_id", nullable = false)
	private Long id;

	@Column(name = "bbs_id", nullable = false)
	private String bbs_id;

	@Column(name = "comments_id", nullable = false)
	private Long comments_id;
	
	@Column(name = "reason", nullable = false, length = 255)
	private String reason;

	@Column(name = "reported_time", nullable = false, updatable = false)
	@CreationTimestamp
	private LocalDate reported_time;

	@Column(name = "proceeded_time", nullable = false, updatable = false)
	@CreationTimestamp
	private LocalDate proceeded_time;
	
	@Column(name = "status", nullable = false, length = 1)
	private String status;
	
	// Getter와 Setter 메소드
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getBbs_id() {
		return bbs_id;
	}

	public void setBbs_id(String bbs_id) {
		this.bbs_id = bbs_id;
	}

	public Long getComments_id() {
		return comments_id;
	}

	public void setComments_id(Long comments_id) {
		this.comments_id = comments_id;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public LocalDate getReported_time() {
		return reported_time;
	}

	public void setReported_time(LocalDate reported_time) {
		this.reported_time = reported_time;
	}
	public LocalDate getProceeded_time() {
		return reported_time;
	}

	public void setProceeded_time(LocalDate proceeded_time) {
		this.proceeded_time = proceeded_time;
	}
	public String getStatus() {
		return reason;
	}

	public void setStatus(String status) {
		this.status = status;
	}


}
