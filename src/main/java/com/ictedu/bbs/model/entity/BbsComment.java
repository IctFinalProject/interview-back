package com.ictedu.bbs.model.entity;

import com.ictedu.user.model.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "BBS_COMMENT")
public class BbsComment {

    @Id
    @SequenceGenerator(name = "seq_comment", sequenceName = "seq_comment", allocationSize = 1, initialValue = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_comment")
    @Column(name = "comment_id", nullable = false)
    private Long commentId;

    @ManyToOne
    @JoinColumn(name = "bbs_id", referencedColumnName = "bbs_id", nullable = false)
    private Bbs bbs;  // Bbs와의 관계 설정

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;  // 작성자

    @Column(name = "content", nullable = false, length = 1000)
    private String content;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "edited_at")
    private LocalDateTime editedAt;

    @Column(name = "deleted", nullable = false)
    private int deleted = 0;  // 소프트 삭제를 위한 필드, 기본값은 0 (삭제되지 않음)

    // 작성자의 username을 반환하는 메서드
    public String getUsername() {
        return user != null ? user.getUsername() : "Anonymous";
    }
}
