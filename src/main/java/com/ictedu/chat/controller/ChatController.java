package com.ictedu.chat.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ictedu.chat.dto.ChatMessagesDTO;
import com.ictedu.chat.dto.ChatRoomDTO;
import com.ictedu.chat.entity.ChatMessages;
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
	
	//유저가 참가 중인 채팅방 목록 출력
	//getChatroomList
	@PostMapping("/userChatrooms")
    public List<ChatRoomDTO> getUserChatrooms(@RequestBody Long userId) {
        List<Long> chatroomIds = chatRoomUsersService.findChatroomIdsByUserId(userId);
        return chatRoomService.findChatroomsByIds(chatroomIds);
    }
	
//	//이전 채팅 목록 출력
//	@PostMapping("/getPastChatting")
//    public List<ChatMessagesDTO> getPastChatting(@RequestBody Long chatroomId) {
//        List<ChatMessages> chatMessages = chatMessagesService.findByChatroomId(chatroomId);
//        return ;
//    }
	
	@DeleteMapping("/exitChatroom")
    public ResponseEntity<Void> exitChatroom(@RequestParam Long currentChatRoomId, @RequestParam Long userId) {
		System.out.println("챗챗 테스트");
		//ChatRoomUser에서 선택한 방과 채팅삭제 요청한 유저가 같이 들어가있는 컬럼 삭제
        chatRoomUsersService.deleteChatRoomUser(currentChatRoomId, userId);
        //요청 유저의 이름을 채팅방에서 삭제해야 함, 근데 제목 수정했을 경우에는 터치 ㄴ isTitleEdited
        chatRoomService.editChatroomTitleExcludeUser(currentChatRoomId, userId);
        
        
        return ResponseEntity.noContent().build();
    }
	
	
	
}
