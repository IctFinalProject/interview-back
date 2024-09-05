package com.ictedu.chat.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ictedu.chat.entity.ChatMessages;

@Repository
public interface ChatMessagesRepository extends JpaRepository<ChatMessages, Long> {
	List<ChatMessages> findByChatroomIdAndIsDeleted(Long chatRoomId, Integer isDeleted);
}
