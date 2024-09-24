package com.ictedu.interview.model.entity;

import java.time.LocalDateTime;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ictedu.resume.entity.ResumeEntity;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Table(name = "videos")
public class VideoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "resume_id")  // 'resume_id' 컬럼을 통해 ResumeEntity와 연결
    private ResumeEntity resume;

    @Column(name = "question_id")
    private Long questionId;
    
    @Lob
    @Column(name = "question_text")
    private String questionText;

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "upload_date")
    private LocalDateTime uploadDate;

    @Column(name = "answer_duration")
    private Double answerDuration;

    // Getters and Setters
}
