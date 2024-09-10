package com.ictedu.adminpage.repository;

import com.ictedu.bbs.model.entity.Bbs;
import com.ictedu.bbs.model.entity.BbsComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminDeletedCommentRepository extends JpaRepository<BbsComment, Long> {

    // 특정 게시글(bbsId)에 속한 deleted 값이 1인 댓글 조회
	List<BbsComment> findByBbsAndDeleted(Bbs bbs, int deleted);

    // 특정 게시글(bbsId)에 속한 모든 댓글 삭제
    void deleteByBbs_BbsId(Long bbsId);
}