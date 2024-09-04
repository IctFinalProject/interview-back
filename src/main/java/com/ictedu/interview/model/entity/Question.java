package com.ictedu.interview.model.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "question")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Question {

	@Id
	@SequenceGenerator(name="seq_interview_question_id",sequenceName = "seq_interview_question_id",allocationSize = 1,initialValue = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "seq_interview_question_id")
    private Long id; // 질문 고유 번호

    @ManyToOne
    @JoinColumn(name = "interview_id", referencedColumnName = "id", nullable = false)
    private Interview interviewId; // 이 질문이 속한 인터뷰

    @Lob
    @Column(name = "question_text", nullable = false)
    private String questionText; // 질문 내용

    @Column(name = "question_type", nullable = false)
    private String questionType; // 질문 유형 (예: COMMON, RESUME, FOLLOW_UP 등)
    
    @Lob
    @Column(name = "question_script", nullable = false)
    private String script;
    
    @Lob
    @Column(name = "keyword", nullable = true)
    private String keywords;
    
    @ColumnDefault("SYSDATE")
    @CreationTimestamp
    @Column(name = "created_time", nullable = false)
    private LocalDateTime createdTime; // 생성 시간

    @Column(name = "updated_time")
    private LocalDateTime updatedTime; // 수정 시간
}
