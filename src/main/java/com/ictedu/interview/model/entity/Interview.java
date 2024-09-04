package com.ictedu.interview.model.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ictedu.user.model.entity.User;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "interview")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Interview {

	@Id
	@SequenceGenerator(name="seq_interview_id",sequenceName = "seq_interview_id",allocationSize = 1,initialValue = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "seq_interview_id")
    private Long id; // 면접 고유 번호

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User userId; // 면접을 보는 사용자 ID

    @Column(name = "interview_type", nullable = false)
    private String interviewType; // 면접 유형 (예: MOCK, REAL)
    
    @Column(name = "start_time")
    private LocalDateTime startTime; // 면접 시작 시간

    @Column(name = "end_time")
    private LocalDateTime endTime; // 면접 종료 시간

    @OneToMany(mappedBy = "interviewId", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Question> questions; // 면접에 해당하는 질문 목록

    @Column(name = "overall_feedback", length = 2000)
    private String overallFeedback; // 전체적인 피드백
    
    @ColumnDefault("SYSDATE")
    @CreationTimestamp
    @Column(name = "created_time", nullable = false)
    private LocalDateTime createdTime; // 생성 시간

    @Column(name = "updated_time")
    private LocalDateTime updatedTime; // 수정 시간
}
