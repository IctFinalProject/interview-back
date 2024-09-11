package com.ictedu.chat.service;

import java.time.LocalDateTime;
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
                                    .isTitleEdited(0)
                                    .updatedTime(LocalDateTime.now())
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
    
    public void editChatroomTitleExcludeUser(Long chatroomId, Long userId) {
    	//if isTitleEdited가 1이면 암것도 안하고용 0이면 이름에서 제외하기 작업 ㄱ
    	
    	ChatRoom chatRoom = chatRoomRepository.findById(chatroomId)
                .orElseThrow(() -> new RuntimeException("ChatRoom not found"));
    	
    	List<Long> userIds = chatRoomUsersRepository.findUserIdsByChatRoomId(chatroomId);
    	List<User> users = userRepository.findAllById(userIds);
    	
    	if(users.size() == 0) {
    		chatRoomRepository.deleteById(chatroomId);
    	}
    	else {
    		if(chatRoom.getIsTitleEdited() == 0) {
            	
            	System.out.println("users.size() : " + users.size());
            	
        		String usernames = users.stream()
                        .map(User::getUsername)
                        .collect(Collectors.joining(", "));
            	
            	System.out.println(usernames);
            	
            	chatRoom.setChatRoomTitle(usernames);
            	chatRoomRepository.save(chatRoom);   	
        	}
    	}
    }
    
//    public void deleteChatroom(Long chatroomId, Long userId) {
//    	ChatRoom chatRoom = chatRoomRepository.findById(chatroomId)
//                .orElseThrow(() -> new RuntimeException("ChatRoom not found"));
//    	
//    	List<Long> userIds = chatRoomUsersRepository.findUserIdsByChatRoomId(chatroomId);
//    	List<User> users = userRepository.findAllById(userIds);
//    	
//    	if(users.size() == 0) {
//    		chatRoomRepository.deleteById(chatroomId);
//    	}
//    	
//    }
    
    public String getChatroomTitle(Long chatRoomId) {
        // 채팅방 ID로 채팅방을 찾음
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElse(null);
        if (chatRoom != null) {
            return chatRoom.getChatRoomTitle();
        } else {
            throw new RuntimeException("Chat room not found");
        }
    }
    
    public void updateChatRoomTitle(Long chatRoomId, String newTitle) {
    	ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid chat room ID: " + chatRoomId));
    	
    	ChatRoomDTO chatRoomDTO = ChatRoomDTO.toDto(chatRoom);
    	chatRoomDTO.setChatRoomTitle(newTitle);
    	chatRoomDTO.setIsTitleEdited(1);
    	
    	ChatRoom updateChatRoomTitle = chatRoomDTO.toEntity();
    	chatRoomRepository.save(updateChatRoomTitle);

    
    }
    
    
}