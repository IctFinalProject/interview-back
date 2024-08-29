package com.ictedu.bot.entity;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "FINE_TUNING_JOBS")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FineTuningJobs {
    @Id
    @Column(name = "JOB_ID")
    private String jobId;

    @Column(name = "STATUS", nullable = false)
    private String status;

    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "COMPLETED_AT")
    private LocalDateTime completedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}