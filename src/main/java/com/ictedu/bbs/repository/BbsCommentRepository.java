package com.ictedu.bbs.repository;

import com.ictedu.bbs.model.entity.BbsComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BbsCommentRepository extends JpaRepository<BbsComment, Long> {

    // 기존 메서드 (필요시 유지)
    List<BbsComment> findByBbs_BbsId(Long bbsId);

    // 추가된 메서드: DELETED가 0인 댓글만 조회
    List<BbsComment> findByBbs_BbsIdAndDeleted(Long bbsId, int deleted);

	List<BbsComment> findByDeleted(int i);
}