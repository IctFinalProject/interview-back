package com.ictedu.bbs.repository;

import com.ictedu.bbs.model.entity.BbsComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BbsCommentRepository extends JpaRepository<BbsComment, Long> {
    List<BbsComment> findByBbs_BbsId(Long bbsId);  // 특정 게시글에 대한 댓글 조회
}
