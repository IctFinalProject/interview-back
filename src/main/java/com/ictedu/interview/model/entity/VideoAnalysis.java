package com.ictedu.interview.model.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "video_analysis")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VideoAnalysis {

    @Id
    @SequenceGenerator(name="seq_video_analysis_id", sequenceName = "seq_video_analysis_id", allocationSize = 1, initialValue = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_video_analysis_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "video_id", referencedColumnName = "id", nullable = false)
    private VideoEntity video; // VideoEntity 엔티티와의 연관 관계
    
    @Lob
    @Column(name = "analyzed_file_path")
    private String analyzedFilePath;

    @Column(name = "head_score")
    private Double headScore;

    @Column(name = "eye_score")
    private Double eyeScore;

    @Column(name = "smile_score")
    private Double smileScore;

    @Column(name = "hand_score")
    private Double handScore;

    @Column(name = "pose_score")
    private Double poseScore;

    @Column(name = "avg_score")
    private Double avgScore;

    @ColumnDefault("SYSDATE")
    @CreationTimestamp
    @Column(name = "analysis_date", nullable = false, updatable = false)
    private LocalDateTime analysisDate;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}