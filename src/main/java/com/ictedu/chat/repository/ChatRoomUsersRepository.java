package com.ictedu.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.ictedu.chat.entity.ChatRoomUsers;

import java.util.List;

@Repository
public interface ChatRoomUsersRepository extends JpaRepository<ChatRoomUsers, Long> {
	
	List<ChatRoomUsers> findByUserId(Long userId);
	ChatRoomUsers findByChatroomIdAndUserId(Long chatroomId, Long userId);
	
	// chatroomId로 userId들을 가져오는 쿼리 메서드
    @Query("SELECT cru.user FROM ChatRoomUsers cru WHERE cru.chatroom = :chatroomId")
    List<Long> findUserIdsByChatRoomId(Long chatroomId);
}