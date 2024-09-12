package com.ictedu.bbs.model.entity;
import java.time.LocalDate;


import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.ictedu.user.model.entity.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapKeyColumn;
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
@Table(name = "BBS") //테이블 이름(게시글)

@Builder
public class Bbs {

	@Id
	@SequenceGenerator(name="seq_bbs",sequenceName = "seq_bbs",allocationSize = 1,initialValue 	= 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "seq_bbs")
	@Column(name = "bbs_id", nullable = false)
	private Long bbsId;

	@ManyToOne
	@JoinColumn(name = "user_id", referencedColumnName = "id", nullable = true)
	private User userId;

	@OneToMany(mappedBy = "bbs", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
	@JsonManagedReference
	private List<BbsComment> comments;

	
	@OneToMany(mappedBy = "bbs", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
	@JsonManagedReference
	private List<BbsReport> reports;

	@Column(name = "title", nullable = false, length = 255)
	private String title;

	@Column(name = "content", nullable = false, length = 2000)
	private String content;

	// 아래가 바꾼거 (날짜 -> 날짜+시간) 
	@ColumnDefault("CURRENT_TIMESTAMP")
	@Column(name = "createdAt", nullable = false, updatable = false)
	@CreationTimestamp
	private LocalDateTime createdAt;


	@Column(name = "hitcount", nullable = false)
	@ColumnDefault("0")
	private Long hitCount = 0L;

	
	@Column(name = "likes", nullable = false)
    @ColumnDefault("0")
    private Long likes = 0L;
	
	
	@Column(name = "active", nullable = false)
	@ColumnDefault("1")
	private Integer active = 1;

	@Column(name = "inactive", nullable = false)
	@ColumnDefault("0")
	private Integer inactive = 0;

	@Column(name = "reported", nullable = false)
	@ColumnDefault("0")
	private Integer reported = 0;

	@Column(name = "deleted", nullable = false)
	@ColumnDefault("0")
	private Integer deleted = 0;

	@Column(name = "deleted_date")
	private LocalDateTime deleted_date;

	@Column(name = "edited", nullable = false)
	private Integer edited = 0;

	@Column(name = "edited_date")
	private LocalDateTime edited_date;

	@Column(name = "type", nullable = false, length = 20)
	private String type;

	@ElementCollection
	@Lob
	@CollectionTable(name = "BBS_FILE", joinColumns = @JoinColumn(name = "bbs_id"))
	@MapKeyColumn(name = "file_name")
	@Column(name = "file_data")
	private Map<String, byte[]> files = new HashMap<>();

	// 앱솔: 신고 또는 일반 삭제를 구분하기 위한 필드 추가
	@Column(name = "deleted_reason", nullable = false)
	@ColumnDefault("0")
	private Integer deletedReason = 0;  // 기본값 0으로 설정

	// Getter와 Setter 메소드
	public Long getBbs_id() {
		return bbsId;
	}

	public void setBbs_id(Long bbs_id) {
		this.bbsId = bbs_id;
	}

	public User getUser_id() {
		return userId;
	}

	public void setUser_id(User userId) {
		this.userId = userId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	// 아래 두개 바꾼거임 (날짜 -> 날짜 +시간) 
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public Long getHitCount() {
		return hitCount;
	}

	public void setHitCount(Long hitCount) {
		this.hitCount = hitCount;
	}
	
	public Long getLikes() {
		return likes;
	}

	public void setLikes(Long likes) {
		this.likes = likes;
	}
	
	public Integer getActive() {
		return active;
	}

	public void setActive(Integer active) {
		this.active = active;
	}

	public Integer getInactive() {
		return inactive;
	}

	public void setInactive(Integer inactive) {
		this.inactive = inactive;
	}

	public Integer getReported() {
		return reported;
	}

	public void setReported(Integer reported) {
		this.reported = reported;
	}

	public Integer getDeleted() {
		return deleted;
	}

	public void setDeleted(Integer deleted) {
		this.deleted = deleted;
	}

	public LocalDateTime getDeleted_date() {
		return deleted_date;
	}

	public void setDeleted_date(LocalDateTime deleted_date) {
		this.deleted_date = deleted_date;
	}

	public Integer getEdited() {
		return edited;
	}

	public void setIsEdited(Integer edited) {
		this.edited = edited;
	}

	public LocalDateTime getEdited_date() {
		return edited_date;
	}

	public void setEdited_date(LocalDateTime edited_date) {
		this.edited_date = edited_date;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Integer getDeletedReason() {  // 앱솔: 신고 삭제 여부 반환
		return deletedReason;
	}

	public void setDeletedReason(Integer deletedReason) {  // 앱솔: 신고 삭제 여부 설정
		this.deletedReason = deletedReason;
	}

	// 작성자의 username을 반환하는 메서드
	public String getUsername() {
		return userId != null ? userId.getUsername() : "Anonymous";
	}
	public void incrementHitcount() {
		this.hitCount += 1;
	}
}
