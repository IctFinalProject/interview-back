package com.ictedu.chat.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ictedu.chat.dto.ChatRoomDTO;
import com.ictedu.chat.entity.ChatRoom;
import com.ictedu.chat.service.ChatRoomService;
import com.ictedu.user.model.entity.User;
import com.ictedu.user.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class ChatController {
	
	private final ChatRoomService chatRoomService;
	
	@PostMapping("/createChatroom")
	public ResponseEntity<ChatRoomDTO> createChatRoom(
			@RequestParam String chatRoomTitle,
			@RequestParam String lastMessage,
			@RequestParam Long[] userIds) {

	    ChatRoomDTO chatRoomDTO = ChatRoomDTO.builder()
	                                          .chatRoomTitle(chatRoomTitle)
	                                          .lastMessage(lastMessage)
	                                          .build();

	    ChatRoomDTO createChatRoomDTO = chatRoomService.createChatRoom(chatRoomDTO, userIds);

	    return ResponseEntity.ok(createChatRoomDTO);
	}
	
}
