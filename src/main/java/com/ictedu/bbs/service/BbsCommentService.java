package com.ictedu.bbs.service;

import com.ictedu.bbs.model.entity.Bbs;
import com.ictedu.bbs.model.entity.BbsComment;
import com.ictedu.bbs.repository.BbsCommentRepository;
import com.ictedu.user.model.entity.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BbsCommentService {

    private final BbsCommentRepository commentRepository;

    @Autowired
    public BbsCommentService(BbsCommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public List<CommentRequestDto> getCommentsByBbsId(Long bbsId) {
        // BbsComment에서 댓글과 함께 user의 username을 가져옴
        return commentRepository.findByBbs_BbsId(bbsId)
            .stream()
            .map(comment -> new CommentRequestDto(
                comment.getCommentId(),
                comment.getContent(),
                comment.getCreatedAt(),
                comment.getUser().getUsername(), // User의 username 가져옴
                comment.getUser().getId() // User의 ID도 함께 가져옴
            ))
            .collect(Collectors.toList());
    }

    public BbsComment createComment(Bbs bbs, String content, User user) {
        BbsComment comment = BbsComment.builder()
                .bbs(bbs)
                .content(content)
                .user(user)
                .createdAt(LocalDateTime.now())
                .build();
        return commentRepository.save(comment);
    }

    public BbsComment updateComment(Long commentId, String content) {
        BbsComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));
        comment.setContent(content);
        comment.setEditedAt(LocalDateTime.now());
        return commentRepository.save(comment);
    }
}
