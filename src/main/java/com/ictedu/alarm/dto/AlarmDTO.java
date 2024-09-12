package com.ictedu.alarm.dto;

import java.time.LocalDateTime;

import com.ictedu.alarm.entity.Alarm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlarmDTO {
    
    private Long id;
    private String type;
    private String title;
    private String content;
    private Long senderId;
    private Long receiverId;
    private Long chatroomId;
    private Long bbsId;
    private Long contentId;
    private Integer isRead;
    private Integer isDisabled;
    private LocalDateTime createdTime;
    
    // DTO to Entity conversion method
    public Alarm toEntity() {
        return Alarm.builder()
                    .id(id)
                    .type(type)
                    .title(title)
                    .content(content)
                    .senderId(senderId)
                    .receiverId(receiverId)
                    .chatroomId(chatroomId)
                    .bbsId(bbsId)
                    .contentId(contentId)
                    .isRead(isRead)
                    .isDisabled(isDisabled)
                    .createdTime(createdTime)
                    .build();
    }

    // Entity to DTO conversion method
    public static AlarmDTO toDto(Alarm alarm) {
        return AlarmDTO.builder()
                       .id(alarm.getId())
                       .type(alarm.getType())
                       .title(alarm.getTitle())
                       .content(alarm.getContent())
                       .senderId(alarm.getSenderId())
                       .receiverId(alarm.getReceiverId())
                       .chatroomId(alarm.getChatroomId())
                       .bbsId(alarm.getBbsId())
                       .contentId(alarm.getContentId())
                       .isRead(alarm.getIsRead())
                       .isDisabled(alarm.getIsDisabled())
                       .createdTime(alarm.getCreatedTime())
                       .build();
    }
}