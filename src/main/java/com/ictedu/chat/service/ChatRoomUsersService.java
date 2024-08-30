package com.ictedu.chat.service;

import com.ictedu.chat.entity.ChatRoom;
import com.ictedu.chat.entity.ChatRoomUsers;
import com.ictedu.chat.repository.ChatRoomRepository;
import com.ictedu.chat.repository.ChatRoomUsersRepository;
import com.ictedu.user.model.entity.User;
import com.ictedu.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatRoomUsersService {

    @Autowired
    private ChatRoomUsersRepository chatRoomUsersRepository;
    
    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private UserRepository userRepository;

    public void createChatRoomUsers(Long chatroomId, List<Long> userIds) {
    	ChatRoom chatRoom = chatRoomRepository.findById(chatroomId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid chatroom ID: " + chatroomId));
    	
        for (Long userId : userIds) {
            User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Invalid user ID: " + userId));
            ChatRoomUsers chatRoomUsers = ChatRoomUsers.builder()
            		.chatroom(chatRoom)
            		.user(user)
            		.build();
            chatRoomUsersRepository.save(chatRoomUsers);
        }
    }
    
    public List<Long> findChatroomIdsByUserId(Long userId) {
        return chatRoomUsersRepository.findByUserId(userId)
                                      .stream()
                                      .map(chatRoomUsers -> chatRoomUsers.getChatroom().getId())
                                      .collect(Collectors.toList());
    }
    
    public void deleteChatRoomUser(Long chatroomId, Long userId) {
        ChatRoomUsers chatRoomUser = chatRoomUsersRepository.findByChatroomIdAndUserId(chatroomId, userId);
        if (chatRoomUser != null) {
            chatRoomUsersRepository.delete(chatRoomUser);
        } else {
            throw new RuntimeException("Chat room user association not found");
        }
    }
    
}