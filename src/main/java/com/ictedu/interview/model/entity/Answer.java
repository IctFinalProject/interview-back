package com.ictedu.interview.model.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "answer")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Answer {

	@Id
	@SequenceGenerator(name="seq_interview_answer_id",sequenceName = "seq_interview_answer_id",allocationSize = 1,initialValue = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "seq_interview_answer_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "question_id", referencedColumnName = "id", nullable = false)
    private Question question;

    @ManyToOne
    @JoinColumn(name = "interview_session_id", referencedColumnName = "id", nullable = false)
    private InterviewSession interviewSession;

    @Lob
    @Column(columnDefinition = "CLOB")
    private String content;

    @Column(name = "video_path")
    private String videoPath;
    
    @ColumnDefault("SYSDATE")
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
