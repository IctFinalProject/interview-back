package com.ictedu.bbs.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.ictedu.user.model.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.ColumnDefault;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "BBS_COMMENT")
public class BbsComment {

	@Id
	@SequenceGenerator(name = "seq_comment", sequenceName = "seq_comment", allocationSize = 1, initialValue = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_comment")
	@Column(name = "comment_id", nullable = false)
	private Long commentId;

	@ManyToOne
	@JoinColumn(name = "bbs_id", referencedColumnName = "bbs_id", nullable = false)
	@JsonBackReference  // 양방향 참조 방지
	private Bbs bbs;


	@ManyToOne
	@JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
	private User user;  // 작성자

	@OneToMany(mappedBy = "comment", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
	@JsonManagedReference
	private List<BbsReport> reports;
	
	
	@Column(name = "content", nullable = false, length = 1000)
	private String content;

	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "edited_at")
	private LocalDateTime editedAt;

	@Column(name = "deleted", nullable = false)
	private int deleted = 0;  // 소프트 삭제를 위한 필드, 기본값은 0 (삭제되지 않음)

	// 앱솔: 신고 또는 일반 삭제를 구분하기 위한 필드 추가
	@Column(name = "deleted_reason", nullable = false)
	@ColumnDefault("0")
	private Integer deletedReason = 0;  // 기본값 0으로 설정

	// 작성자의 username을 반환하는 메서드
	public String getUsername() {
		return user != null ? user.getUsername() : "Anonymous";
	}

	public Integer getDeletedReason() {  // 앱솔: 신고 삭제 여부 반환
		return deletedReason;
	}

	public void setDeletedReason(Integer deletedReason) {  // 앱솔: 신고 삭제 여부 설정
		this.deletedReason = deletedReason;
	}

}