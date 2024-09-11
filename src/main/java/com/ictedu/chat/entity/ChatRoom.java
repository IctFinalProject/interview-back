package com.ictedu.chat.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "chatroom") //테이블 이름
public class ChatRoom {
	
	@Id
	@SequenceGenerator(name="seq_chatroom",sequenceName = "seq_chatroom",allocationSize = 1,initialValue = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "seq_chatroom")
	private Long id;
	
	@Column(name = "chatroom_title", nullable = true)
    private String chatRoomTitle;
	
	@ColumnDefault("0")
	@Column(name = "is_title_edited")
    private Integer isTitleEdited;
	
    @ColumnDefault("SYSDATE")
    @CreationTimestamp
    @Column(name = "created_time")
    private LocalDateTime createdTime;
    
    //@Column(name = "deleted_time", nullable = true)
    //private LocalDateTime deletedTime;
    
    @Column(name = "last_message", nullable = true)
    private String lastMessage;
    
    @Column(name = "updated_time")
    private LocalDateTime updatedTime;

}
