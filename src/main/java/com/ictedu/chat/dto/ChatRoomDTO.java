package com.ictedu.chat.dto;

import java.time.LocalDateTime;

import com.ictedu.chat.entity.ChatRoom;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomDTO {
	
    private Long id;
    private String chatRoomTitle;
    private String lastMessage;
    private LocalDateTime createdTime;  // 필요한 경우 LocalDateTime으로 변경 가능
    private LocalDateTime deletedTime;

    public ChatRoom toEntity() {
        return ChatRoom.builder()
                       .id(id)
                       .chatRoomTitle(chatRoomTitle)
                       .lastMessage(lastMessage)
                       .createdTime(createdTime)
                       .deletedTime(deletedTime)
                       .build();
    }
    
    public static ChatRoomDTO toDto(ChatRoom chatRoom) {
        return ChatRoomDTO.builder()
                          .id(chatRoom.getId())
                          .chatRoomTitle(chatRoom.getChatRoomTitle())
                          .lastMessage(chatRoom.getLastMessage())
                          .createdTime(chatRoom.getCreatedTime())
                          .deletedTime(chatRoom.getDeletedTime() != null ? chatRoom.getDeletedTime() : null)
                          .build();
    }


}