package com.ictedu.interview.model.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "claude_analysis")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaudeAnalysis {
	@Id
    @SequenceGenerator(name="seq_claude_analysis_id", sequenceName = "seq_claude_analysis_id", allocationSize = 1, initialValue = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_claude_analysis_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "video_id", referencedColumnName = "id", nullable = false)
    private VideoEntity video;

    @Lob
    @Column(name = "analysis_data")
    private String analysisData;

    @ColumnDefault("SYSDATE")
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // 전체 점수를 저장하기 위한 필드 추가
    @Column(name = "overall_score")
    private Double overallScore;

    // 개선 제안사항을 저장하기 위한 필드 추가
    @Lob
    @Column(name = "improvement_suggestions")
    private String improvementSuggestions;
    
    // 키워드 필드 추가
    @Lob
    @Column(name = "keywords")
    private String keywords;

	/*
	 * // Big Five 분석 결과 필드 추가
	 * 
	 * @Lob
	 * 
	 * @Column(name = "big_five_analysis") private String bigFiveAnalysis;
	 */
}
