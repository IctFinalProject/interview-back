package com.ictedu.adminpage.repository;

import com.ictedu.bbs.model.entity.Bbs;
import com.ictedu.bbs.model.entity.BbsComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AdminDeletedPostRepository extends JpaRepository<Bbs, Long> {
	
	// deleted 값이 1인 게시글 (삭제된 게시글) 조회
    List<Bbs> findByDeleted(int deleted);

  

}