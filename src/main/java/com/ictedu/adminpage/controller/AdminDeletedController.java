package com.ictedu.adminpage.controller;


import com.ictedu.adminpage.service.AdminDeletedPostService;
import com.ictedu.bbs.service.BbsCommentDTO;
import com.ictedu.bbs.service.BbsDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admindeleted")
public class AdminDeletedController {

    @Autowired
    private AdminDeletedPostService adminDeletedPostService;

    // 모든 삭제된 게시글 조회 엔드포인트 (BbsDTO 사용)
    @GetMapping("/deletedposts")
    public ResponseEntity<List<BbsDto>> getDeletedPosts() {
        List<BbsDto> deletedPosts = adminDeletedPostService.getDeletedPosts();  // DTO 사용
        return ResponseEntity.ok(deletedPosts);
    }

    // 특정 삭제된 게시글과 댓글을 함께 조회하는 엔드포인트 (DTO 사용)
    @GetMapping("/deleted/{bbsId}/with-comments")
    public ResponseEntity<Map<String, Object>> getDeletedPostWithComments(@PathVariable Long bbsId) {
        Map<String, Object> postWithComments = adminDeletedPostService.getPostWithComments(bbsId); // 내부에서 DTO 변환
        return ResponseEntity.ok(postWithComments);
    }

    // 특정 삭제된 게시글을 영구 삭제하는 엔드포인트
    @DeleteMapping("/deleted/{bbsId}")
    public ResponseEntity<Void> deletePostPermanently(@PathVariable Long bbsId) {
        adminDeletedPostService.deletePostPermanently(bbsId);
        return ResponseEntity.ok().build();
    }

    // 특정 삭제된 댓글을 영구 삭제하는 엔드포인트
    @DeleteMapping("/delete/{commentId}")
    public ResponseEntity<Void> deleteCommentPermanently(@PathVariable Long commentId) {
        adminDeletedPostService.deleteCommentPermanently(commentId);
        return ResponseEntity.ok().build();
    }

    // 삭제된 게시글을 복구하는 엔드포인트
    @PostMapping("/restorepost/{bbsId}")
    public ResponseEntity<Void> restorePost(@PathVariable Long bbsId) {
        adminDeletedPostService.restorePost(bbsId);
        return ResponseEntity.ok().build();
    }

    // 삭제된 댓글을 복구하는 엔드포인트
    @PostMapping("/restorecomment/{commentId}")
    public ResponseEntity<Void> restoreComment(@PathVariable Long commentId) {
        adminDeletedPostService.restoreComment(commentId);
        return ResponseEntity.ok().build();
    }

    // 삭제된 댓글 조회 엔드포인트 (BbsCommentDTO 사용)
    @GetMapping("/deleted-comments")
    public ResponseEntity<List<BbsCommentDTO>> getDeletedComments() {
        List<BbsCommentDTO> deletedComments = adminDeletedPostService.getDeletedComments();  // DTO 사용
        return ResponseEntity.ok(deletedComments);
    }
}
