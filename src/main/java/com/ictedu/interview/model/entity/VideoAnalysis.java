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

    @Column(name = "head_score", nullable = true)
    private Double headScore;

    @Column(name = "eye_score", nullable = true)
    private Double eyeScore;


    @Column(name = "avg_score")
    private Double avgScore;
    
    // 오디오 분석 결과 필드 추가
    @Column(name = "audio_pitch")
    private Double audioPitch;

    @Column(name = "audio_tempo")
    private Double audioTempo;

    @Column(name = "audio_volume")
    private Double audioVolume;

    @Column(name = "audio_spectral_centroid")
    private Double audioSpectralCentroid;

    @ColumnDefault("SYSDATE")
    @CreationTimestamp
    @Column(name = "analysis_date", nullable = false, updatable = false)
    private LocalDateTime analysisDate;

}