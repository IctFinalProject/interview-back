package com.ictedu.chat.dto;

import com.ictedu.chat.entity.ChatRoom;
import com.ictedu.chat.entity.ChatRoomUsers;
import com.ictedu.user.dto.UserDTO;
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
public class ChatRoomUsersDTO {

    private Long id;
    private User user;
    private ChatRoom chatroom;
    
    public ChatRoomUsers toEntity() {
        return ChatRoomUsers.builder()
                            .id(id)
                            .user(user)
                            .chatroom(chatroom)
                            .build();
    }

    public static ChatRoomUsersDTO toDto(ChatRoomUsers chatRoomUsers) {
        return ChatRoomUsersDTO.builder()
                               .id(chatRoomUsers.getId())
                               .user(chatRoomUsers.getUser())
                               .chatroom(chatRoomUsers.getChatroom())
                               .build();
    }

    
}