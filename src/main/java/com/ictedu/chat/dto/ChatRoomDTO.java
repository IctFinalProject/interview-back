package com.ictedu.chat.dto;

import java.time.LocalDateTime;

import com.ictedu.chat.entity.ChatRoom;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomDTO {
	
    private Long id;
    private String chatRoomTitle;
    private Integer isTitleEdited;
    private String lastMessage;
    private LocalDateTime createdTime;  // 필요한 경우 LocalDateTime으로 변경 가능
    private LocalDateTime updatedTime;
    

    public ChatRoom toEntity() {
        return ChatRoom.builder()
                       .id(id)
                       .chatRoomTitle(chatRoomTitle)
                       .isTitleEdited(isTitleEdited)
                       .lastMessage(lastMessage)
                       .createdTime(createdTime)
                       .updatedTime(createdTime)
                       .build();
    }
    
    public static ChatRoomDTO toDto(ChatRoom chatRoom) {
        return ChatRoomDTO.builder()
                          .id(chatRoom.getId())
                          .chatRoomTitle(chatRoom.getChatRoomTitle())
                          .isTitleEdited(chatRoom.getIsTitleEdited())
                          .lastMessage(chatRoom.getLastMessage())
                          .createdTime(chatRoom.getCreatedTime())
                          .updatedTime(chatRoom.getUpdatedTime())
                          .build();
    }


}