package com.ictedu.chat.service;

import com.ictedu.chat.dto.ChatMessagesDTO;
import com.ictedu.chat.dto.ChatRoomDTO;
import com.ictedu.chat.entity.ChatMessages;
import com.ictedu.chat.entity.ChatRoom;
import com.ictedu.chat.repository.ChatMessagesRepository;
import com.ictedu.user.model.entity.User;
import com.ictedu.user.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ChatMessagesService {

    @Autowired
    private ChatMessagesRepository chatMessagesRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Map<String, Object>> getPastChatting(Long chatRoomId) {
        List<ChatMessages> chatMessages = chatMessagesRepository.findByChatroomIdAndIsDeleted(chatRoomId, 0);

        return chatMessages.stream().map(chatMessage -> {
            User user = userRepository.findById(chatMessage.getUser().getId()).orElse(null);
            //String username = user.getUsername();
            
            Map<String, Object> messageData = new HashMap<>();
            messageData.put("id", chatMessage.getId());
            messageData.put("message", chatMessage.getMessage());
            messageData.put("username", user != null ? user.getUsername() : "탈퇴한 유저");
            messageData.put("createdTime", chatMessage.getCreatedTime());
            messageData.put("userId", chatMessage.getUser().getId());
            messageData.put("chatroomId", chatMessage.getChatroom().getId());
            
            return messageData;
        }).collect(Collectors.toList());
    }
}