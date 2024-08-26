package com.ictedu.chat.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import com.ictedu.user.model.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "chat_messages") //테이블 이름
public class ChatMessages {
	
	@Id
	@SequenceGenerator(name="seq_chatroom_messages",sequenceName = "seq_chatroom_messages",allocationSize = 1,initialValue = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "seq_chatroom_messages")
	private Long id;
	
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id", referencedColumnName = "id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatroom_id", referencedColumnName = "id", nullable = false)
    private ChatRoom chatroom;
	
	@Column(name = "message")
    private LocalDateTime message;
	
    @ColumnDefault("SYSDATE")
    @CreationTimestamp
    @Column(name = "created_time")
    private LocalDateTime createdTime;
    
    @Column(name = "deleted_time")
    private LocalDateTime deletedTime;

}
