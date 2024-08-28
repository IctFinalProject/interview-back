package com.ictedu.chat.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ictedu.chat.dto.ChatRoomDTO;
import com.ictedu.chat.entity.ChatRoom;
import com.ictedu.chat.service.ChatRoomService;
import com.ictedu.chat.service.ChatRoomUsersService;
import com.ictedu.user.model.entity.User;
import com.ictedu.user.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class ChatController {
	
	@Autowired
	private ChatRoomService chatRoomService;
	
	@Autowired
	private ChatRoomUsersService chatRoomUsersService;
	
	@PostMapping("/createChatroom")
    public void createChatroom(@RequestBody List<Long> userIds) {
        Long chatroomId = chatRoomService.createChatRoom(userIds);
        chatRoomUsersService.createChatRoomUsers(chatroomId, userIds);
    }
	
	@GetMapping("/allChatroomList")
    public List<ChatRoom> getAllChatRooms() {
        return chatRoomService.getAllChatRooms(); 
    }
	
	@PostMapping("/userChatrooms")
    public List<ChatRoomDTO> getUserChatrooms(@RequestBody Long userId) {
        List<Long> chatroomIds = chatRoomUsersService.findChatroomIdsByUserId(userId);
        return chatRoomService.findChatroomsByIds(chatroomIds);
    }
	
}
