package com.ictedu.bot.entity;

import lombok.*;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "BOT")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bot {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "BOT_SEQ")
	@SequenceGenerator(name = "BOT_SEQ", sequenceName = "BOT_SEQ", allocationSize = 1)
	@Column(name = "BOT_ID")
    private Long botId;

    @Column(name = "ID", nullable = false)
    private Long id;
    
    @ColumnDefault("SYSDATE")
    @CreationTimestamp
    @Column(name = "CREATED_TIME", nullable = false)
    private LocalDateTime createdTime;
    
    @ColumnDefault("SYSDATE")
    @CreationTimestamp
    @Column(name = "LAST_UPDATED_TIME", nullable = false)
    private LocalDateTime lastUpdatedTime;
    
    @Column(name = "ENDED_TIME")
    private LocalDateTime endedTime;
    
    @OneToMany(mappedBy = "bot", cascade = CascadeType.ALL)
    @Builder.Default
    private List<BotQuestion> questions = new ArrayList<>();

    @OneToMany(mappedBy = "bot", cascade = CascadeType.ALL)
    @Builder.Default
    private List<BotFile> files = new ArrayList<>();

//    @PrePersist
//    protected void onCreate() {
//        createdTime = LocalDateTime.now();
//        lastUpdatedTime = LocalDateTime.now();
//    }

//    @PreUpdate
//    protected void onUpdate() {
//        
//    }

//	public void setStatus(String status) {
//		// TODO Auto-generated method stub
//		
//	}  
}