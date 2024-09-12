package com.ictedu.adminpage.service;

import com.ictedu.adminpage.repository.AdminDeletedCommentRepository;
import com.ictedu.adminpage.repository.AdminDeletedPostRepository;
import com.ictedu.bbs.model.entity.Bbs;
import com.ictedu.bbs.model.entity.BbsComment;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AdminDeletedPostService {

	@Autowired
	private AdminDeletedPostRepository adminDeletedPostRepository;

	@Autowired
	private AdminDeletedCommentRepository adminDeletedCommentRepository;  // 댓글 저장을 위한 리포지토리

	// 삭제된 게시글을 조회
	public List<Bbs> getDeletedPosts() {
		return adminDeletedPostRepository.findByDeleted(1);  // 삭제된 게시글(1)만 조회
	}

	// 특정 게시글과 관련 댓글을 조회
	public Map<String, Object> getPostWithComments(Long bbsId) {
		Optional<Bbs> post = adminDeletedPostRepository.findById(bbsId);

		if (post.isPresent()) {
			List<BbsComment> comments = adminDeletedCommentRepository.findByBbsAndDeleted(post.get(), 1);
			Map<String, Object> result = new HashMap<>();
			result.put("post", post.get());
			result.put("comments", comments);
			return result;
		}

		return null; // 혹은 적절한 예외 처리
	}

	// 특정 게시글을 영구 삭제
	@Transactional //트랜잭션을 보장
	public void deletePostPermanently(Long bbsId) {
		adminDeletedCommentRepository.deleteByBbs_BbsId(bbsId);
		adminDeletedPostRepository.deleteById(bbsId);
	}
	
	@Transactional
	public void deleteCommentPermanently(Long commentId) {
	    Optional<BbsComment> comment = adminDeletedCommentRepository.findById(commentId);

	    if (comment.isPresent()) {
	        adminDeletedCommentRepository.deleteById(commentId); // 댓글 영구 삭제
	    }
	}

	
	// 삭제된 게시글을 복구
	@Transactional
	public void restorePost(Long bbsId) {
		Optional<Bbs> post = adminDeletedPostRepository.findById(bbsId);

		if (post.isPresent()) {
			Bbs bbs = post.get();

			//게시글 복구: deleted_reason 값이 0일 때만 복구
			if (bbs.getDeletedReason() == 0) {  
				bbs.setDeleted(0);  // 삭제 상태 복구
				adminDeletedPostRepository.save(bbs);

				//게시글과 연결된 댓글도 함께 복구
				List<BbsComment> comments = adminDeletedCommentRepository.findByBbsAndDeleted(bbs, 1);
				for(BbsComment comment : comments) {
					if(comment.getDeletedReason() ==0) { //댓글도 deleted_reason값이 0일 때만 복구
						comment.setDeleted(0); //삭제 상태 복구
						adminDeletedCommentRepository.save(comment);
					}
				}
			} else {
				// 신고로 삭제된 경우에는 복구하지 않음
				System.out.println("신고로 삭제된 게시글은 복구할 수 없습니다.");
			}
		} else {
			System.out.println("해당 게시글을 찾을 수 없습니다.");
		}
	}

	@Transactional
	public void restoreComment(Long commentId) {
	    Optional<BbsComment> optionalComment = adminDeletedCommentRepository.findById(commentId);

	    if (optionalComment.isPresent()) {
	        BbsComment bbsComment = optionalComment.get();

	        // 게시글이 삭제된 경우 댓글 복구 불가
	        Bbs associatedPost = bbsComment.getBbs();
	        if (associatedPost.getDeleted() == 1) {
	            System.out.println("게시글이 삭제되어 댓글을 복구할 수 없습니다.");
	            return;
	        }

	        // 댓글 복구: deleted_reason 값이 0일 때만 복구
	        if (bbsComment.getDeletedReason() == 0) {
	            bbsComment.setDeleted(0);  // 댓글의 deleted 값을 0으로 설정
	            adminDeletedCommentRepository.save(bbsComment);  // 댓글 저장
	        } else {
	            System.out.println("신고로 인해 삭제된 댓글은 복구할 수 없습니다.");
	        }
	    } else {
	        System.out.println("해당 댓글을 찾을 수 없습니다.");
	    }
	}
	//댓글 조회
	public List<BbsComment> getDeletedComments() {
	    List<BbsComment> comments = adminDeletedCommentRepository.findByDeleted(1); // deleted가 1인 댓글 조회

	    // 댓글과 연결된 게시글 제목은 이미 댓글 객체 내 Bbs 객체에서 가져올 수 있음
	    return comments;
	}

}