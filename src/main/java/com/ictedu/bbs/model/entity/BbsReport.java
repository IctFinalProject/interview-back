package com.ictedu.bbs.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bbs_id", referencedColumnName = "bbs_id", nullable = false)
    private Bbs bbs;

    @Column(name = "reason", nullable = false, length = 255)
    private String reason;

    @Column(name = "additional_info", length = 1000)
    private String additionalInfo;

    @Column(name = "reported_at", nullable = false, updatable = false)
    private LocalDateTime reportedAt;

    @Column(name = "proceeded_time", nullable = true)  // nullable = true 설정
    private LocalDateTime proceededTime;

    @Column(name = "status", nullable = false, length = 10)
    private String status;

    @PrePersist
    protected void onCreate() {
        this.reportedAt = LocalDateTime.now();  // 신고된 날짜와 시간 기본값
    }
}
