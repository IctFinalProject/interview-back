package com.ictedu.adminpage.service;

import com.ictedu.adminpage.repository.AdminDeletedCommentRepository;
import com.ictedu.adminpage.repository.AdminDeletedPostRepository;
import com.ictedu.bbs.dto.BbsCommentDTO;
import com.ictedu.bbs.dto.BbsDTO;
import com.ictedu.bbs.model.entity.Bbs;
import com.ictedu.bbs.model.entity.BbsComment;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AdminDeletedPostService {

    @Autowired
    private AdminDeletedPostRepository adminDeletedPostRepository;

    @Autowired
    private AdminDeletedCommentRepository adminDeletedCommentRepository;

    // 삭제된 게시글을 조회할 때 DTO로 변환해서 반환
    public List<BbsDTO> getDeletedPosts() {
        List<Bbs> posts = adminDeletedPostRepository.findByDeleted(1);
        return posts.stream()
                .map(this::convertToBbsDTO)
                .collect(Collectors.toList());
    }

    // 특정 게시글과 관련 댓글을 조회할 때 DTO로 변환해서 반환
    public Map<String, Object> getPostWithComments(Long bbsId) {
        Optional<Bbs> post = adminDeletedPostRepository.findById(bbsId);

        return post.map(b -> {
            List<BbsComment> comments = adminDeletedCommentRepository.findByBbsAndDeleted(b, 1);
            Map<String, Object> result = new HashMap<>();
            result.put("post", convertToBbsDTO(b));  // 게시글을 DTO로 변환
            result.put("comments", comments.stream()
                    .map(this::convertToBbsCommentDTO)
                    .collect(Collectors.toList()));
            return result;
        }).orElse(null); // 존재하지 않을 경우 null 반환 또는 예외 처리
    }

    // 특정 게시글을 영구 삭제
    @Transactional
    public void deletePostPermanently(Long bbsId) {
        adminDeletedCommentRepository.deleteByBbs_BbsId(bbsId);
        adminDeletedPostRepository.deleteById(bbsId);
    }

    @Transactional
    public void deleteCommentPermanently(Long commentId) {
        adminDeletedCommentRepository.findById(commentId).ifPresent(adminDeletedCommentRepository::delete);
    }

    // 게시글과 관련 댓글 복구
    @Transactional
    public void restorePost(Long bbsId) {
        Optional<Bbs> post = adminDeletedPostRepository.findById(bbsId);

        post.ifPresent(bbs -> {
            if (bbs.getDeletedReason() == 0) {
                bbs.setDeleted(0);
                adminDeletedPostRepository.save(bbs);

                // 게시글과 연결된 댓글 복구
                List<BbsComment> comments = adminDeletedCommentRepository.findByBbsAndDeleted(bbs, 1);
                comments.forEach(comment -> {
                    if (comment.getDeletedReason() == 0) {
                        comment.setDeleted(0);
                        adminDeletedCommentRepository.save(comment);
                    }
                });
            } else {
                throw new IllegalStateException("신고로 삭제된 게시글은 복구할 수 없습니다.");
            }
        });
    }

    @Transactional
    public void restoreComment(Long commentId) {
        adminDeletedCommentRepository.findById(commentId).ifPresent(comment -> {
            Bbs associatedPost = comment.getBbs();
            if (associatedPost.getDeleted() == 1) {
                throw new IllegalStateException("게시글이 삭제되어 댓글을 복구할 수 없습니다.");
            }
            if (comment.getDeletedReason() == 0) {
                comment.setDeleted(0);
                adminDeletedCommentRepository.save(comment);
            } else {
                throw new IllegalStateException("신고로 삭제된 댓글은 복구할 수 없습니다.");
            }
        });
    }

    // 삭제된 댓글 조회할 때 DTO로 변환하여 반환
    public List<BbsCommentDTO> getDeletedComments() {
        List<BbsComment> comments = adminDeletedCommentRepository.findByDeleted(1);
        return comments.stream()
                .map(this::convertToBbsCommentDTO)
                .collect(Collectors.toList());
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

    // 엔티티 -> BbsCommentDTO 변환 메서드
    private BbsCommentDTO convertToBbsCommentDTO(BbsComment comment) {
        return BbsCommentDTO.builder()
                .commentId(comment.getCommentId())
                .content(comment.getContent())
                .username(comment.getUsername())
                .bbsTitle(comment.getBbs().getTitle())
                .createdAt(comment.getCreatedAt())
                .deletedAt(comment.getDeletedAt())
                .build();
    }
}