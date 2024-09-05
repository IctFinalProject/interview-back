package com.ictedu.bbs.service;

import com.ictedu.bbs.model.entity.Bbs;
import com.ictedu.bbs.model.entity.BbsComment;
import com.ictedu.bbs.repository.BbsCommentRepository;
import com.ictedu.user.model.entity.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BbsCommentService {

    private final BbsCommentRepository commentRepository;

    @Autowired
    public BbsCommentService(BbsCommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public List<BbsComment> getCommentsByBbsId(Long bbsId) {
        return commentRepository.findByBbs_BbsId(bbsId);
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
    
    public void deleteComment(Long commentId) {
        BbsComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));
        commentRepository.delete(comment);
    }

}
