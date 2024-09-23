package com.ictedu.interview.model.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;

@Entity
@Table(name = "final_analysis")
public class FinalAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "final_analysis_seq")
    @SequenceGenerator(name = "final_analysis_seq", sequenceName = "SEQ_FINAL_ANALYSIS_ID", allocationSize = 1)
    private Long id;

    @Column(name = "video_id", nullable = false)
    private Long videoId;

    @Column(name = "overall_score", nullable = false)
    private Double overallScore;

    @Column(name = "video_score", nullable = false)
    private Double videoScore;

    @Column(name = "speech_score", nullable = false)
    private Double speechScore;

    @Column(name = "content_score", nullable = false)
    private Double contentScore;

    @Column(name = "strengths", columnDefinition = "CLOB")
    private String strengths;

    @Column(name = "weaknesses", columnDefinition = "CLOB")
    private String weaknesses;

    @Column(name = "improvement_suggestions", columnDefinition = "CLOB")
    private String improvementSuggestions;

    @ColumnDefault("SYSDATE")
    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // Getters and setters
}