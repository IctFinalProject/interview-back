//package com.ictedu.chat.dto;
//
//import com.ictedu.chat.entity.ChatRoom;
//import com.ictedu.chat.entity.ChatRoomUsers;
//import com.ictedu.user.model.entity.User;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//@Data
//@Builder
//@NoArgsConstructor
//@AllArgsConstructor
//public class ChatRoomUsersDTO {
//
//    private Long id;
//    private UserDTO user;
//    private ChatRoomDTO chatroom;
//
//    // Entity -> DTO 변환
//    public static ChatRoomUsersDTO toDto(ChatRoomUsers chatRoomUsers) {
//        return ChatRoomUsersDTO.builder()
//                               .id(chatRoomUsers.getId())
//                               .user(UserDTO.toDto(chatRoomUsers.getUser()))
//                               .chatroom(ChatRoomDTO.toDto(chatRoomUsers.getChatroom()))
//                               .build();
//    }
//
//    // DTO -> Entity 변환
//    public static ChatRoomUsers toEntity(ChatRoomUsersDTO chatRoomUsersDTO) {
//        return ChatRoomUsers.builder()
//                            .id(chatRoomUsersDTO.getId())
//                            .user(UserDTO.toEntity(chatRoomUsersDTO.getUser()))
//                            .chatroom(ChatRoomDTO.toEntity(chatRoomUsersDTO.getChatroom()))
//                            .build();
//    }
//}