package com.ictedu.bbs.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

import com.ictedu.bbs.model.entity.Bbs;
import com.ictedu.user.model.entity.User;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BbsDto {

	private Long id;
    private Long bbsId;             // 게시글 ID
    private String title;           // 게시글 제목
    private String content;         // 게시글 내용
    private String username;        // 작성자 이름
    private Long hitCount;          // 조회수
    private Long likes;             // 좋아요 수
    private LocalDateTime createdAt; // 게시글 생성 날짜
    private LocalDateTime deletedAt; // 게시글 삭제 날짜 (삭제된 경우)
    private Integer deletedReason;  // 삭제 이유 (일반 삭제인지, 신고로 인한 삭제인지)
    private String status;          // 게시글 상태 (VISIBLE, HIDDEN 등)
    private User userId;            // 작성자 ID
    private Integer active;         // 활성화 상태
    private Integer inactive;       // 비활성화 상태
    private Integer reported;       // 신고 여부
    private Integer deleted;        // 삭제 여부
    private Integer edited;         // 수정 여부
    private LocalDateTime editedDate; // 수정 날짜
    private String type;            // 게시글 타입
    private List<FileBbsDto> files; // 파일 리스트

    
    public BbsDto(String title, String content, User user) {
        this.title = title;
        this.content = content;
        this.userId = user;
    }
    
    // Bbs 엔티티 -> BbsDTO 변환 메서드
    public static BbsDto toDto(Bbs bbs) {
        return BbsDto.builder()
                .bbsId(bbs.getBbsId())
                .title(bbs.getTitle())
                .content(bbs.getContent())
                .username(bbs.getUserId() != null ? bbs.getUserId().getUsername() : "Unknown")
                .hitCount(bbs.getHitCount())
                .likes(bbs.getLikes())
                .createdAt(bbs.getCreatedAt())
                .deletedAt(bbs.getDeleted_date())
                .deletedReason(bbs.getDeletedReason())
                .status(bbs.getStatus())
                .userId(bbs.getUserId())
                .active(bbs.getActive())
                .inactive(bbs.getInactive())
                .reported(bbs.getReported())
                .deleted(bbs.getDeleted())
                .edited(bbs.getEdited())
                .editedDate(bbs.getEdited_date())
                .type(bbs.getType())
                .build();
    }

    // DTO -> Bbs 엔티티 변환 메서드
    public Bbs toEntity() {
        return Bbs.builder()
                .bbsId(bbsId)
                .userId(userId)
                .title(title)
                .content(content)
                .createdAt(createdAt)
                .hitCount(hitCount)
                .likes(likes)
                .active(active)
                .inactive(inactive)
                .reported(reported)
                .deleted(deleted)
                .deleted_date(deletedAt)
                .deletedReason(deletedReason)
                .status(status)
                .edited(edited)
                .edited_date(editedDate)
                .type(type)
                .build();
    }
}
