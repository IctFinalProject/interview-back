package com.ictedu.bbs.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.ictedu.user.model.entity.User;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "BBS_Report") // 테이블 이름(게시글 신고)
public class BbsReport {

    @Id
    @SequenceGenerator(name = "seq_bbs_report_id", sequenceName = "seq_bbs_report_id", allocationSize = 1, initialValue = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_bbs_report_id")
    @Column(name = "report_id", nullable = false)
    private Long id;

    // 게시글과의 연관 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bbs_id", referencedColumnName = "bbs_id", nullable = false)
    @JsonBackReference
    private Bbs bbs;

    // 게시글 등록 날짜 가져오는 메서드
    public LocalDateTime getBbsCreatedAt() {
        return this.bbs != null ? this.bbs.getCreatedAt() : null;
    }

    // 게시글 번호 가져오는 메서드
    public Long getBbsId() {
        return this.bbs != null ? this.bbs.getBbsId() : null;
    }

    // 게시글 제목 가져오는 메서드
    public String getBbsTitle() {
        return this.bbs != null ? this.bbs.getTitle() : null;
    }

    // 댓글과의 연관 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", referencedColumnName = "comment_id", nullable = true)
    @JsonBackReference
    private BbsComment comment;

    // commentId 받아옴
    public Long getCommentId() {
        return this.comment != null ? this.comment.getCommentId() : null;
    }

    // 신고자와의 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)  // 신고자 ID
    private User reporter;

    // 신고 사유
    @Column(name = "reason", nullable = false, length = 255)
    private String reason;

    // 추가 정보
    @Column(name = "additional_info", length = 1000)
    private String additionalInfo;

    // 신고 날짜
    @Column(name = "reported_at", nullable = false, updatable = false)
    private LocalDateTime reportedAt;

    // 처리 시간
    @Column(name = "proceeded_time", nullable = true)  // nullable = true 설정
    private LocalDateTime proceededTime;

    // 신고 상태를 Enum으로 관리 (PENDING, HIDDEN, VISIBLE)
    @Enumerated(EnumType.STRING)  // EnumType.STRING을 사용하여 문자열로 저장
    @Column(name = "status", nullable = false)
    private Status status;

    // 신고 생성 시 기본값 설정
    @PrePersist
    protected void onCreate() {
        this.reportedAt = LocalDateTime.now();  // 신고된 날짜와 시간 기본값
        this.status = Status.PENDING;  // 기본 상태는 PENDING으로 설정
    }

    // 신고 처리 시간 업데이트
    @PreUpdate
    protected void onUpdate() {
        if (this.status == Status.HIDDEN) {
            this.proceededTime = LocalDateTime.now(); // 숨김 처리된 경우 처리 시간 업데이트
        }
    }

    // 게시글 상태를 관리할 enum
    public enum Status {
        PENDING,  // 처리 대기 상태
        HIDDEN,   // 숨김 처리된 상태
        VISIBLE   // 노출 상태
    }
}
