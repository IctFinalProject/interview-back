package com.ictedu.bot.entity;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "BOT_QUESTION")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BotQuestion {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "BOT_QUESTION_SEQ")
	@SequenceGenerator(name = "BOT_QUESTION_SEQ", sequenceName = "BOT_QUESTION_SEQ", allocationSize = 1)
	@Column(name = "QUESTION_ID")
	private Long questionId;

    @Lob
    @Column(name = "CONTENT", nullable = false)
    private String content;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BOT_ID", nullable = false)
    private Bot bot;

    @OneToOne(mappedBy = "question", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private BotAnswer answer;

    @PrePersist
    protected void onCreate() {
        createdTime = LocalDateTime.now();
    }
}