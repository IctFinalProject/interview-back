package com.ictedu.bot.entity;

import lombok.*;
import java.time.LocalDateTime;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "BOT_ANSWER_FEEDBACK")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BotAnswerFeedback {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bot_answer_feedback_seq")
    @SequenceGenerator(name = "bot_answer_feedback_seq", sequenceName = "BOT_ANSWER_FEEDBACK_SEQ", allocationSize = 1)
    private Long feedbackId;

    @Column(name = "LIKE_COUNT", nullable = false)
    @Builder.Default
    private Integer likes = 0;

    @Column(name = "DISLIKES", nullable = false)
    @Builder.Default
    private Integer dislikes = 0;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ANSWER_ID", nullable = false, unique = true)
    private BotAnswer answer;

    @CreationTimestamp
    @Column(name = "CREATED_TIME", nullable = false, updatable = false)
    private LocalDateTime createdTime;

    @UpdateTimestamp
    @Column(name = "LAST_UPDATED_Time", nullable = false)
    private LocalDateTime lastUpdatedTime;

    @Lob
    @Column(name = "USER_COMMENT")
    private String comment;

    @Column(name = "ID")
    private Long userid;

    public BotAnswerFeedback(BotAnswer answer) {
        this.answer = answer;
        this.likes = 0;
        this.dislikes = 0;
    }

    public void incrementLikes() {
        this.likes++;
    }

    public void incrementDislikes() {
        this.dislikes++;
    }
}