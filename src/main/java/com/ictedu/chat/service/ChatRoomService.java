package com.ictedu.chat.service;

import org.springframework.stereotype.Service;

import com.ictedu.chat.dto.ChatRoomDTO;
import com.ictedu.chat.entity.ChatRoom;
import com.ictedu.chat.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;

    public ChatRoomDTO createChatRoom(ChatRoomDTO chatRoomDTO) {
        // DTO -> Entity 변환
        ChatRoom chatRoom = ChatRoomDTO.toEntity(chatRoomDTO);

        // 데이터베이스에 저장
        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);

        // Entity -> DTO 변환 후 반환
        return ChatRoomDTO.toDto(savedChatRoom);
    }
}