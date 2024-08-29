package com.ictedu.bbs.service;

import java.time.LocalDate;




import java.time.LocalDateTime;
import java.util.List;

import com.ictedu.bbs.model.entity.Bbs;
import com.ictedu.bbs.service.BbsDto;
import com.ictedu.user.model.entity.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BbsDto {
	//엔터티의 필드와 일치하지 않아도 무방 즉 필요한 필드만으로 구성
	private Long id;
	private User userId;
	private String username;
	private String title;
	private String content;
	private LocalDate createdAt;
	private Long hitCount;
	private Integer active;
	private Integer inactive;
	private Integer reported;
	private Integer deleted;
	private LocalDateTime deleted_date;
	private Integer edited;
	private LocalDateTime edited_date;
	private String type;
	private List<FileBbsDto> files;
	
	
	//DTO를 ENTITY로 변환하는 메소드
	public Bbs toEntity() {
		return Bbs.builder()
				.bbsId(id)
				.userId(userId)
				.title(title)
				.content(content)
				.createdAt(createdAt)
				.hitCount(hitCount)
				.active(active)
				.inactive(inactive)
				.reported(reported)
				.deleted(deleted)
				.deleted_date(deleted_date)
				.edited(edited)
				.edited_date(edited_date)
				.type(type)
				.build();
	}
	
	//ENTITY를 DTO로 변환하는 메소드
	public static BbsDto toDto(Bbs bbs) {
		return BbsDto.builder()
				.id(bbs.getBbsId())
				.userId(bbs.getUserId())
				.title(bbs.getTitle())
				.content(bbs.getContent())
				.createdAt(bbs.getCreatedAt())
				.hitCount(bbs.getHitCount())
				.active(bbs.getActive())
				.inactive(bbs.getInactive())
				.reported(bbs.getReported())
				.deleted(bbs.getDeleted())
				.deleted_date(bbs.getDeleted_date())
				.edited(bbs.getEdited())
				.edited_date(bbs.getEdited_date())
				.type(bbs.getType())
				.build();
	}
	
    // Constructor, getters, and setters
    public BbsDto(String title, String content, User user) {
        this.title = title;
        this.content = content;
        this.userId = user;
    }
	
	
}
