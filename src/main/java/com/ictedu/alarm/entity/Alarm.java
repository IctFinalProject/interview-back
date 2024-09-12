package com.ictedu.alarm.entity;

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
@Table(name = "alarm")
public class Alarm {
	
	@Id
	@SequenceGenerator(name="seq_alarm",sequenceName = "seq_alarm",allocationSize = 1,initialValue = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "seq_alarm")
	private Long id;
	
	@Column(name = "type")
    private String type;
	
	@Column(name = "title")
    private String title;
	
	@Column(name = "content")
    private String content;
	
	@Column(name = "sender_id")
    private Long senderId; // 알림보낸 사람의 id
	
	@Column(name = "receiver_id")
    private Long receiverId; // 알림받는 사람의 id
	
	@Column(name = "chatroom_id")
    private Long chatroomId; // 채팅방 id (채팅 알림일 경우)
	
	@Column(name = "bbs_id")
    private Long bbsId; // 게시판 id (게시판 알림일 경우)
	
	@Column(name = "content_id")
    private Long contentId; //게시판의 경우 댓글 id, 채팅의 경우 채팅메세지의 id
	
	@ColumnDefault("0")
	@Column(name = "is_read")
    private Integer isRead;
	
	@ColumnDefault("0")
	@Column(name = "is_disabled")
    private Integer isDisabled;
	
    @ColumnDefault("SYSDATE")
    @CreationTimestamp
    @Column(name = "created_time")
    private LocalDateTime createdTime;
	
	
}
