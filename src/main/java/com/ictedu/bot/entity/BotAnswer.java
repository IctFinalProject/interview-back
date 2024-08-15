package com.ictedu.bot.entity;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "BOT_ANSWER")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BotAnswer {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "BOT_ANSWER_SEQ")
	@SequenceGenerator(name = "BOT_ANSWER_SEQ", sequenceName = "BOT_ANSWER_SEQ", allocationSize = 1)
	@Column(name = "ANSWER_ID")
    private Long answerId;

    @Lob
    @Column(name = "CONTENT", nullable = false)
    private String content;

    @Column(name = "CREATED_TIME", nullable = false, updatable = false)
    private LocalDateTime createdTime;

    @ManyToOne
    @JoinColumn(name = "BOT_ID", nullable = false)
    private Bot bot;

    @OneToOne
    @JoinColumn(name = "QUESTION_ID", nullable = false)
    private BotQuestion question;
    
    @OneToOne(mappedBy = "answer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private BotAnswerFeedback feedback;

    @PrePersist
    protected void onCreate() {
        createdTime = LocalDateTime.now();
    }
}