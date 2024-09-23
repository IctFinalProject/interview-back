package com.ictedu.bbs.repository;

import com.ictedu.bbs.model.entity.BbsComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BbsCommentRepository extends JpaRepository<BbsComment, Long> {

    // 특정 게시글의 삭제되지 않은 댓글만 조회 (deleted가 0인 경우)
    List<BbsComment> findByBbs_BbsIdAndDeleted(Long bbsId, int deleted);

    // 특정 게시글의 삭제되지 않았고 상태가 VISIBLE인 댓글만 조회
    List<BbsComment> findByBbs_BbsIdAndDeletedAndStatus(Long bbsId, int deleted, String status);

    // 삭제된 댓글 조회
    List<BbsComment> findByDeleted(int deleted);

    // 상태가 특정 값인 댓글 조회 (ex: VISIBLE, HIDDEN 등)
    List<BbsComment> findByStatus(String status);
    
    List<BbsComment> findByBbs_BbsId(Long bbsId);
    
    List<BbsComment> findByUserIdAndDeleted(Long id, int deleted);
}
