package com.ictedu.adminpage.controller;

import java.util.List;
import java.util.Map;

import com.ictedu.adminpage.service.AdminDeletedPostService;
import com.ictedu.bbs.model.entity.Bbs;
import com.ictedu.bbs.model.entity.BbsComment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admindeleted")
public class AdminDeletedController {

	@Autowired
	private AdminDeletedPostService adminDeletedPostService;

	//모든 삭제된 게시글 조회 엔드포인트
	@GetMapping("/deletedposts")
	public ResponseEntity<List<Bbs>> getDeletedPosts() {
		List<Bbs> deletedPosts = adminDeletedPostService.getDeletedPosts();
		return ResponseEntity.ok(deletedPosts);
	}
	// 특정 삭제된 게시글과 댓글을 함께 조회하는 엔드포인트
	@GetMapping("/deleted/{bbsId}/with-comments")
	public ResponseEntity<Map<String, Object>> getDeletedPostWithComments(@PathVariable Long bbsId) {
		Map<String, Object> postWithComments = adminDeletedPostService.getPostWithComments(bbsId);
		return ResponseEntity.ok(postWithComments);
	}

	// 특정 삭제된 게시글을 영구 삭제하는 엔드포인트
	@DeleteMapping("/deleted/{bbsId}")
	public ResponseEntity<Void> deletePostPermanently(@PathVariable Long bbsId) {
		adminDeletedPostService.deletePostPermanently(bbsId);
		return ResponseEntity.ok().build();
	}
	//특정 삭제된 댓글을 영구 삭제하는 엔드포인트
	@DeleteMapping("/delete/{commentId}")
	public ResponseEntity<Void> deleteCommentPermanently(@PathVariable Long commentId){
		adminDeletedPostService.deleteCommentPermanently(commentId);
		return ResponseEntity.ok().build();
	}

	// 삭제된 게시글을 롤백하는 엔드포인트
	@PostMapping("/restorepost/{bbsId}")
	public ResponseEntity<Void> restorePost(@PathVariable Long bbsId) {
		adminDeletedPostService.restorePost(bbsId);
		return ResponseEntity.ok().build();
	}
	
	// 삭제된 댓글을 롤백하는 엔드포인트
		@PostMapping("/restorecomment/{commentId}")
		public ResponseEntity<Void> restoreComment(@PathVariable Long commentId) {
			adminDeletedPostService.restoreComment(commentId);
			return ResponseEntity.ok().build();
			
		}
	// 삭제된 댓글 조회 엔드포인트
	@GetMapping("/deleted-comments")
	public ResponseEntity<List<BbsComment>> getDeletedComments() {
		List<BbsComment> deletedComments = adminDeletedPostService.getDeletedComments();
		return ResponseEntity.ok(deletedComments);
	}
	

}
