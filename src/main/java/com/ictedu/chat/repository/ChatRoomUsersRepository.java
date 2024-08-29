package com.ictedu.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.ictedu.chat.entity.ChatRoomUsers;

import java.util.List;

@Repository
public interface ChatRoomUsersRepository extends JpaRepository<ChatRoomUsers, Long> {
	List<ChatRoomUsers> findByUserId(Long userId);
}