package com.ictedu.alarm.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ictedu.alarm.entity.Alarm;
import com.ictedu.alarm.repository.AlarmRepository;

import jakarta.transaction.Transactional;

@Service
public class AlarmService {
	
	@Autowired
	private AlarmRepository alarmRepository;

	public List<Alarm> findByUserId(Long userId) {
		return alarmRepository.findByReceiverIdAndIsDisabledAndIsReadOrderByCreatedTimeDesc(userId, 0, 0);
	}
	
	public List<Alarm> findChatAlarmByUserId(Long userId) {
		return alarmRepository.findChatAlarmByReceiverIdAndIsRead(userId);
	}
	
	@Transactional
    public void disableAllAlarms(Long userId, String type) {
		if ("all".equals(type)) {
            alarmRepository.disableAllAlarms(userId);
        } else if ("chat".equals(type)) {
            alarmRepository.disableAlarmsByType(userId, "chat");
        } else if ("bbs".equals(type)) {
            alarmRepository.disableAlarmsByType(userId, "bbs");
        }
    }
	
	@Transactional
    public void disableAlarm(Long id) {
		alarmRepository.disableAlarm(id);
    }
	
	@Transactional
	public void readAlarm(Long id) {
		alarmRepository.readAlarm(id);
	}
	
	@Transactional
	public void readChatAlarm(Long userId, Long chatroomId) {
		alarmRepository.readAlarmInChatroom(userId, chatroomId);
	}

}
