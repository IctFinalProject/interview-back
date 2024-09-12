package com.ictedu.adminpage.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ictedu.bbs.model.entity.BbsComment;
import com.ictedu.bbs.model.entity.BbsReport;

@Repository
public interface AdminReportedCommentRepository extends JpaRepository<BbsReport,Long> {
	//신고된 댓글만 조회하는 쿼리 추가
	List<BbsReport> findByCommentIsNotNull();
	

}
