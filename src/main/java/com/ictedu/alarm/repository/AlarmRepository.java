package com.ictedu.alarm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ictedu.alarm.entity.Alarm;

import jakarta.transaction.Transactional;

@Repository
public interface AlarmRepository extends JpaRepository<Alarm, Long>{
	List<Alarm> findByReceiverIdAndIsDisabledAndIsReadOrderByCreatedTimeDesc(Long receiverId, Integer isDisabled, Integer isRead);
	
	@Query("SELECT a FROM Alarm a WHERE a.receiverId = :userId AND a.isRead = 0 AND a.type = 'chat'")
	List<Alarm> findChatAlarmByReceiverIdAndIsRead(Long userId);
	
	@Modifying
    @Transactional
    @Query("UPDATE Alarm a SET a.isDisabled = 1 WHERE a.receiverId = :userId")
    void disableAllAlarms(Long userId);

    @Modifying
    @Transactional
    @Query("UPDATE Alarm a SET a.isDisabled = 1 WHERE a.receiverId = :userId AND a.type = :type")
    void disableAlarmsByType(Long userId, String type);
    
    @Modifying
    @Transactional
    @Query("UPDATE Alarm a SET a.isDisabled = 1 WHERE a.id = :id")
    void disableAlarm(Long id);
    
    @Modifying
    @Transactional
    @Query("UPDATE Alarm a SET a.isRead = 1 WHERE a.receiverId = :id")
    void readAlarm(Long id);
    
    @Modifying
    @Transactional
    @Query("UPDATE Alarm a SET a.isRead = 1 WHERE a.receiverId = :id AND a.chatroomId = :chatroomId")
    void readAlarmInChatroom(Long id, Long chatroomId);
}
