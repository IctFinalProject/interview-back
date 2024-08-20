package com.ictedu.chat.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ictedu.chat.dto.ChatRoomDTO;
import com.ictedu.chat.entity.ChatRoom;
import com.ictedu.chat.service.ChatRoomService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {
	
	private final ChatRoomService chatRoomService;
	
	@PostMapping("/chatroom")
	public ResponseEntity<ChatRoomDTO> createChatRoom(
			@RequestParam String chatRoomTitle,
			@RequestParam String lastMessage) {

	    ChatRoomDTO chatRoomDTO = ChatRoomDTO.builder()
	                                          .chatRoomTitle(chatRoomTitle)
	                                          .lastMessage(lastMessage)
	                                          .build();

	    ChatRoomDTO createChatRoomDTO = chatRoomService.createChatRoom(chatRoomDTO);

	    return ResponseEntity.ok(createChatRoomDTO);
	}
	
}
