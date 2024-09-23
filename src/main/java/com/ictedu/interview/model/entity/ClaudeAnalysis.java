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

    @Column(name = "overall_score")
    private Double overallScore;

    @Lob
    @Column(name = "improvement_suggestions")
    private String improvementSuggestions;

    @Lob
    @Column(name = "keywords")
    private String keywords;

    @Lob
    @Column(name = "answer_duration_analysis")
    private String answerDurationAnalysis;

    @Column(name = "answer_duration")
    private Double answerDuration;
}