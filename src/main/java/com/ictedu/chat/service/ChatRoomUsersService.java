package com.ictedu.chat.service;

import com.ictedu.chat.entity.ChatRoom;
import com.ictedu.chat.entity.ChatRoomUsers;
import com.ictedu.chat.repository.ChatRoomRepository;
import com.ictedu.chat.repository.ChatRoomUsersRepository;
import com.ictedu.user.model.entity.User;
import com.ictedu.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    
    public List<Map<String, Object>> findUsersByChatroomId(Long chatroomId, Long userId) {
    	System.out.println("내아이디: " + userId);
    	List<Long> userIds = chatRoomUsersRepository.findUserIdsByChatRoomId(chatroomId);
    	// userIds.remove(userId);
    	System.out.println("userIds: " + userIds);
    	
    	List<User> users = userRepository.findAllById(userIds);
//    	
    	List<Map<String, Object>> result = new ArrayList<>();

        for (User user : users) {
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("id", user.getId());
            userMap.put("email", user.getEmail());
            userMap.put("username", user.getUsername());
            userMap.put("address", user.getAddress());
            userMap.put("birth", user.getBirth());
            userMap.put("gender", user.getGender());
            userMap.put("phone", user.getPhone());

            // 프로필 이미지를 "data:image/jpeg;base64," 형식으로 변환
            if (user.getProfileImage() != null) {
                String base64Image = Base64.getEncoder().encodeToString(user.getProfileImage()); // 이미 Base64로 인코딩된 값이라고 가정
                userMap.put("profileImage", "data:image/jpeg;base64," + base64Image);
            } else {
                userMap.put("profileImage", null);
            }

            result.add(userMap);
        }
        System.out.println("결과 출력!");
        return result;
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