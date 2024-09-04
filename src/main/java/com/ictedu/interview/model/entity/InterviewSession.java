package com.ictedu.interview.model.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import com.ictedu.user.model.entity.User;  // User 엔티티의 올바른 import
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "interview_session")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewSession {

	@Id
	@SequenceGenerator(name="seq_interview_session_id",sequenceName = "seq_interview_session_id",allocationSize = 1,initialValue = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "seq_interview_session_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User userId;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;
    
    @ColumnDefault("SYSDATE")
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}

