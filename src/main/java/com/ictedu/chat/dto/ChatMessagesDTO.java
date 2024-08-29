package com.ictedu.chat.dto;

import java.time.LocalDateTime;

import com.ictedu.chat.entity.ChatMessages;
import com.ictedu.chat.entity.ChatRoom;
import com.ictedu.user.model.entity.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessagesDTO {
	
	private Long id;
	private User user;
	private ChatRoom chatroom;
	private String message;
	private Integer isDeleted;
    private LocalDateTime createdTime;
    private LocalDateTime deletedTime;
    
    // DTO를 엔티티로 변환하는 메서드
    public ChatMessages toEntity() {
        return ChatMessages.builder()
                           .id(id)
                           .user(user)
                           .chatroom(chatroom)
                           .message(message)
                           .isDeleted(isDeleted)
                           .createdTime(createdTime)
                           .deletedTime(deletedTime)
                           .build();
    }

    // 엔티티를 DTO로 변환하는 메서드
    public static ChatMessagesDTO toDto(ChatMessages chatMessages) {
        return ChatMessagesDTO.builder()
                              .id(chatMessages.getId())
                              .user(chatMessages.getUser())
                              .chatroom(chatMessages.getChatroom())
                              .message(chatMessages.getMessage())
                              .isDeleted(chatMessages.getIsDeleted())
                              .createdTime(chatMessages.getCreatedTime())
                              .deletedTime(chatMessages.getDeletedTime())
                              .build();
    }
	

}
