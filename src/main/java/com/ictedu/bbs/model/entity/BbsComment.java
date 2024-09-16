package com.ictedu.bbs.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class BbsComment {

    @Id
    @SequenceGenerator(name = "seq_comment", sequenceName = "seq_comment", allocationSize = 1, initialValue = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_comment")
    @Column(name = "comment_id", nullable = false)
    private Long commentId;

    // BBS 엔티티와의 관계 (댓글이 달린 게시글)
    @ManyToOne
    @JoinColumn(name = "bbs_id", referencedColumnName = "bbs_id", nullable = false)
    @JsonBackReference  // 양방향 참조 방지
    @JsonIgnore
    private Bbs bbs;

    // 작성자 정보
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    // 댓글과 관련된 신고 목록
    @OneToMany(mappedBy = "comment", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<BbsReport> reports;

    // 댓글 내용
    @Column(name = "content", nullable = false, length = 1000)
    private String content;

    // 댓글 작성 시간
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // 댓글 수정 시간
    @Column(name = "edited_at")
    private LocalDateTime editedAt;

    // 상태 (숨김, 노출 관리)
    @Column(name = "status", nullable = false, length = 10)
    private String status = "VISIBLE";  // 기본값은 노출 상태

    // 댓글 삭제 여부 (소프트 삭제)
    @Column(name = "deleted", nullable = false)
    private int deleted = 0;  // 기본값 0: 삭제되지 않음

    // 삭제된 이유 (신고 등)
    @Column(name = "deleted_reason", nullable = false)
    @ColumnDefault("0")
    private Integer deletedReason = 0;

    // 삭제된 시간
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // 작성자의 username을 반환하는 메서드
    public String getUsername() {
        return user != null ? user.getUsername() : "Anonymous";
    }

    // 댓글이 달린 게시글의 제목을 반환하는 메서드
    public String getBbsTitle() {
        return bbs != null ? bbs.getTitle() : "No Title";
    }

    // 삭제된 이유 반환
    public Integer getDeletedReason() {
        return deletedReason;
    }

    // 삭제된 이유 설정
    public void setDeletedReason(Integer deletedReason) {
        this.deletedReason = deletedReason;
    }

    // 삭제 날짜 반환
    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    // 상태 조회 메서드 (노출/숨김 관리)
    public String getStatus() {
        return status;
    }

    // 상태 설정 메서드 (노출/숨김 관리)
    public void setStatus(String status) {
        this.status = status;
    }

    // 댓글 수정 시간 설정 메서드
    public void setEditedAt(LocalDateTime editedAt) {
        this.editedAt = editedAt;
    }
}
