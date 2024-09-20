package com.ictedu.bbs.service;

import com.ictedu.bbs.model.entity.Bbs;
import com.ictedu.bbs.model.entity.BbsComment;
import com.ictedu.bbs.repository.BbsCommentRepository;
import com.ictedu.user.model.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BbsCommentService {

    private final BbsCommentRepository commentRepository;

    @Autowired
    public BbsCommentService(BbsCommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public List<BbsComment> getCommentsByBbsId(Long bbsId) {
        // deleted 값이 0인(삭제되지 않은) 댓글만 조회
        return commentRepository.findByBbs_BbsIdAndDeleted(bbsId, 0);
    }

    public BbsComment createComment(Bbs bbs, String content, User user) {
        BbsComment comment = BbsComment.builder()
                .bbs(bbs)
                .content(content)
                .user(user)
                .createdAt(LocalDateTime.now())
                .deleted(0)  // 기본값 0 설정 (삭제되지 않음)
                .deletedReason(0)  // 삭제 사유 기본값 0
                .status("VISIBLE")  // 상태 기본값 VISIBLE 설정
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
        comment.setDeleted(1);  // 소프트 삭제로 변경
        comment.setStatus("HIDDEN");  // 삭제된 댓글은 상태를 HIDDEN으로 변경
        comment.setDeletedAt(LocalDateTime.now());  // 삭제된 시간 기록
        commentRepository.save(comment);
    }

    public Optional<BbsComment> findById(Long commentId) {
        return commentRepository.findById(commentId);
    }

    public List<BbsComment> findAllDeletedComments() {
        return commentRepository.findByDeleted(1); // deleted 값이 1인 댓글만 조회
    }
}
