package com.ictedu.interview.model.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "feedback")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Feedback {

	@Id
	@SequenceGenerator(name="seq_interview_feedback_id",sequenceName = "seq_interview_feedback_id",allocationSize = 1,initialValue = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "seq_interview_feedback_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "answer_id", referencedColumnName = "id", nullable = false)
    private Answer answer;

    @Column(name = "evaluation_score")
    private Integer evaluationScore;

    @Lob
    @Column(columnDefinition = "CLOB")
    private String comments;
    
    @ColumnDefault("SYSDATE")
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}