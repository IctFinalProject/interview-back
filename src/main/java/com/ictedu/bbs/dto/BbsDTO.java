package com.ictedu.bbs.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import com.ictedu.bbs.model.entity.Bbs;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BbsDTO {

    private Long bbsId;             // 게시글 ID
    private String title;           // 게시글 제목
    private String content;         // 게시글 내용
    private String username;        // 작성자 이름
    private Long hitCount;          // 조회수
    private Long likes;             // 좋아요 수
    private LocalDateTime createdAt; // 게시글 생성 날짜
    private LocalDateTime deletedAt; // 게시글 삭제 날짜 (삭제된 경우)
    private Integer deletedReason;  // 삭제 이유 (일반 삭제인지, 신고로 인한 삭제인지)
    
    // Bbs 엔티티 -> BbsDTO 변환 메서드
    public static BbsDTO toDto(Bbs bbs) {
        return BbsDTO.builder()
                .bbsId(bbs.getBbsId())
                .title(bbs.getTitle())
                .content(bbs.getContent())
                .username(bbs.getUserId() != null ? bbs.getUserId().getUsername() : "Unknown") // 작성자 이름 매핑
                .hitCount(bbs.getHitCount())
                .likes(bbs.getLikes())
                .createdAt(bbs.getCreatedAt())
                .deletedAt(bbs.getDeleted_date())
                .deletedReason(bbs.getDeletedReason())
                .build();
    }
    
 // 엔티티 -> BbsDTO 변환 메서드
    private BbsDTO convertToBbsDTO(Bbs bbs) {
        return BbsDTO.builder()
                .bbsId(bbs.getBbsId())
                .title(bbs.getTitle())
                .content(bbs.getContent())
                .username(bbs.getUserId() != null ? bbs.getUserId().getUsername() : "Unknown") // 작성자 이름 매핑
                .createdAt(bbs.getCreatedAt())
                .deletedAt(bbs.getDeleted_date())
                .build();
    }
}