package com.ictedu.chat.service;

import org.springframework.stereotype.Service;

import com.ictedu.chat.dto.ChatRoomDTO;
import com.ictedu.chat.entity.ChatRoom;
import com.ictedu.chat.entity.ChatRoomUsers;
import com.ictedu.chat.repository.ChatRoomRepository;
import com.ictedu.chat.repository.ChatRoomUsersRepository;
import com.ictedu.user.model.entity.User;
import com.ictedu.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomUsersRepository chatRoomUsersRepository;
    private final UserRepository userRepository;

    public ChatRoomDTO createChatRoom(ChatRoomDTO chatRoomDTO, Long[] userIds) {
        // DTO -> Entity 변환
        ChatRoom chatRoom = ChatRoomDTO.toEntity(chatRoomDTO);

        // 데이터베이스에 저장
        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);

        
        // 선택된 userId로 ChatRoomUsers 엔터티 생성 및 저장
        for (Long userId : userIds) {
            User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Invalid user ID: " + userId));
            ChatRoomUsers chatRoomUsers = new ChatRoomUsers();
            chatRoomUsers.setUser(user);
            chatRoomUsers.setChatroom(savedChatRoom);
            chatRoomUsersRepository.save(chatRoomUsers);
        }
        
        
        
        // Entity -> DTO 변환 후 반환
        return ChatRoomDTO.toDto(savedChatRoom);
    }
}