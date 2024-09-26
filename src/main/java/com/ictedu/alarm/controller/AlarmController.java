package com.ictedu.alarm.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ictedu.alarm.entity.Alarm;
import com.ictedu.alarm.service.AlarmService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/alarm")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class AlarmController {
	
	@Autowired
	private AlarmService alarmService;
	
	@PostMapping("/getAlarm")
    public List<Alarm> getAlarm(@RequestBody Long userId) {
        return alarmService.findByUserId(userId);
    }
	
	@PostMapping("/disableAlarm")
    public ResponseEntity<String> disableAlarm(@RequestBody Long id) {
		try {
            alarmService.disableAlarm(id);
            return ResponseEntity.ok("All alarms disabled successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error disabling alarm: " + e.getMessage());
        }
    }
	
	@PostMapping("/disableAllAlarms")
    public ResponseEntity<String> disableAllAlarms(@RequestBody Map<String, Object> request) {
		System.out.println("뭔데용: " + request.get("userId"));
		System.out.println("뭔데용 타입: " + request.get("userId").getClass().getName());
		
		// Long userId = (Long.parseLong((String) request.get("userId")));
		
		Object userIdObj = request.get("userId");
		Long userId = null;

		if (userIdObj instanceof String) {
		    userId = Long.parseLong((String) userIdObj);
		} else if (userIdObj instanceof Integer) {
		    userId = ((Integer) userIdObj).longValue();
		}
		
        String type = ((String) request.get("type"));
        try {
            alarmService.disableAllAlarms(userId, type);
            return ResponseEntity.ok("All alarms disabled successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error disabling alarms: " + e.getMessage());
        }
    }
	
	@PostMapping("/readAllAlarms")
    public ResponseEntity<String> readAllAlarms(@RequestBody Long id) {
		try {
            alarmService.readAlarm(id);
            return ResponseEntity.ok("All alarms disabled successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error disabling alarm: " + e.getMessage());
        }
    }
	
	@PostMapping("/getChatAlarm")
	public List<Alarm> getChatAlarm(@RequestBody Long userId) {
        return alarmService.findChatAlarmByUserId(userId);
    }
	
	// 채팅방 들어갔을때 알람 isRead 1 설정하기
	@PostMapping("/readChatAlarmInChatroom")
    public ResponseEntity<String> readChatAlarmInChatroom(@RequestBody Map<String, Long> request) {
		System.out.println();
		Long userId =  request.get("userId");
		Long chatroomId = request.get("chatroomId");
        
        try {
            alarmService.readChatAlarm(userId, chatroomId);
            return ResponseEntity.ok("All alarms disabled successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error disabling alarms: " + e.getMessage());
        }
    }
	
	
}
