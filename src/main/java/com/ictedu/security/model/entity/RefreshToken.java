package com.ictedu.security.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

import com.ictedu.user.model.entity.User;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Data
@Table(name = "refresh_tokens")
public class RefreshToken {

    @Id
    @SequenceGenerator(name="seq_token_id",sequenceName = "seq_token_id",allocationSize = 1,initialValue = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "seq_token_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id", referencedColumnName = "id", nullable = false)
    private User user;

    @Column(name = "token_value", nullable = false, length = 255)
    private String tokenValue;

    @Column(name = "created_time", nullable = false)
    private LocalDateTime createdTime;

    @Column(name = "expires_time", nullable = false)
    private Long expiresTime;

    @Column(name = "expirated_date", nullable = false)
    private LocalDateTime expiratedDate;

    @Column(name = "user_agent")
    private String userAgent;

    @Column(length = 50)
    private String status;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        if (createdTime == null) createdTime = now;
        if (expiresTime == null) expiresTime = 0L; // 만약 expiresTime이 null일 경우 기본 값 설정
        if (expiratedDate == null) {
            expiratedDate = now.plusSeconds(expiresTime / 1000); // 예를 들어 expiresTime이 밀리초 단위라면
        }
    }
}


