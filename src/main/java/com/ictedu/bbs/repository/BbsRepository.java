package com.ictedu.bbs.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ictedu.bbs.model.entity.Bbs;
import com.ictedu.user.model.entity.User;

@Repository
public interface BbsRepository extends JpaRepository<Bbs, Long>{
	List<Bbs> findByActiveTrue();
    List<Bbs> findByUserIdAndActiveTrue(User userId);
    List<Bbs> findByDeleted(int deleted);  // 삭제 여부로 필터링된 게시글 조회
	
    
	

}
