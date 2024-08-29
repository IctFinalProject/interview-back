package com.ictedu.bot.entity;

import lombok.*;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ANSWER_ID", nullable = false, unique = true)
    private BotAnswer answer;

    @Column(name = "LIKE_COUNT", nullable = false)
    @Builder.Default
    private Integer likes = 0;

    @Column(name = "DISLIKES", nullable = false)
    @Builder.Default
    private Integer dislikes = 0;

    @Column(name = "RELEVANCE_SCORE")
    private Integer relevanceScore; // 1-5 scale

    @Column(name = "CLARITY_SCORE")
    private Integer clarityScore; // 1-5 scale

    @Lob
    @Column(name = "USER_COMMENT")
    private String comment;

    @Column(name = "ID")
    private Long userId;

    @CreationTimestamp
    @Column(name = "CREATED_TIME", nullable = false, updatable = false)
    private LocalDateTime createdTime;

    @UpdateTimestamp
    @Column(name = "LAST_UPDATED_TIME", nullable = false)
    private LocalDateTime lastUpdatedTime;

    public void incrementLikes() {
        this.likes++;
    }

    public void incrementDislikes() {
        this.dislikes++;
    }
    public BotAnswerFeedback(BotAnswer answer) {
        this.answer = answer;
        this.likes = 0;
        this.dislikes = 0;
        this.relevanceScore = 0;
        this.clarityScore = 0;
    }
}