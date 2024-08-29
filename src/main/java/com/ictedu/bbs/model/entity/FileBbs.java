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
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "File-Bbs") //테이블 이름
public class FileBbs {

	@Id
	@SequenceGenerator(name="seq_id",sequenceName = "seq_id",allocationSize = 1,initialValue = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "seq_id")
	@Column(name = "BbsFile_id", nullable = false)
	private Long BbsFileId;

	@ManyToOne
	@JoinColumn(name = "bbs_id", nullable = false)
	private Bbs bbs;

	@Column(name = "file_id", nullable = false)
	private String fileId;

	@Column(name = "created_time", nullable = false, updatable = false)
	@CreationTimestamp
	private LocalDateTime created_time;

	// Getter와 Setter 메소드
	public Long getBbsFile_id() {
		return BbsFileId;
	}

	public void setBbsFile_id(Long BbsFileId) {
		this.BbsFileId = BbsFileId;
	}

	public Bbs getBbsId() {
        return bbs;
    }

    public void setBbsId(Bbs bbs) {
        this.bbs = bbs;
    }

	public String getFile_id() {
		return fileId;
	}

	public void setFile_id(String fileId) {
		this.fileId = fileId;
	}

	public LocalDateTime getCreated_time() {
		return created_time;
	}

	public void setCreated_time(LocalDateTime created_time) {
		this.created_time = created_time;
	}

	
}