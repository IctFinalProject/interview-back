package com.ictedu.chat.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    
    @Transactional
    public Long createChatRoom(List<Long> userIds) {
    	
    	List<User> users = userRepository.findAllById(userIds);
    	String usernames = users.stream()
                .map(User::getUsername)
                .collect(Collectors.joining(", "));
    	
        ChatRoom chatRoom = ChatRoom.builder()
                                    .chatRoomTitle(usernames)  // 기본 이름, 필요시 수정 가능
                                    .build();
        
        chatRoomRepository.save(chatRoom);
        return chatRoom.getId();  // 생성된 채팅방의 ID 반환
    }
    
    public List<ChatRoom> getAllChatRooms() {
        return chatRoomRepository.findAll();
    }
    
    public List<ChatRoomDTO> findChatroomsByIds(List<Long> chatroomIds) {
        List<ChatRoom> chatrooms = chatRoomRepository.findAllById(chatroomIds);
        return chatrooms.stream()
                        .map(ChatRoomDTO::toDto)
                        .collect(Collectors.toList());
    }
    
    
}