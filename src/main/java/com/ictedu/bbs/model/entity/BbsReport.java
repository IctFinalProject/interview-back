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

    // 댓글과의 연관 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", referencedColumnName = "comment_id", nullable = true)
    @JsonBackReference
    private BbsComment comment;

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

    // 신고 상태 (PENDING, HIDDEN, VISIBLE 등 상태 관리)
    @Column(name = "status", nullable = false, length = 20)
    private String status = "PENDING";

    // 신고 생성 시 기본값 설정
    @PrePersist
    protected void onCreate() {
        this.reportedAt = LocalDateTime.now();  // 신고된 날짜와 시간 기본값
        this.status = "PENDING";  // 기본 상태는 PENDING으로 설정
    }

   //신고 상태에 따른 처리 시간 업데이트 메서드 추가
    public void updateStatus(String status) {
    	this.status = status;
    	if(status.equals("HIDDEN") || status.equals("RESOLVED")) {
    		this.proceededTime = LocalDateTime.now();//처리 완료 시간
    	}
    }
}
