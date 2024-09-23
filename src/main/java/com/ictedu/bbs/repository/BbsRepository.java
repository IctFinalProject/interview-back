package com.ictedu.bbs.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ictedu.bbs.model.entity.Bbs;
import com.ictedu.user.model.entity.User;

@Repository
public interface BbsRepository extends JpaRepository<Bbs, Long> {
    
    // 상태가 VISIBLE인 게시글만 조회
    List<Bbs> findByStatus(String status);

    // 삭제된 게시글만 조회
    List<Bbs> findByDeleted(int deleted);  

    // 특정 사용자가 작성한 게시글 중 상태가 VISIBLE인 게시글 조회
    List<Bbs> findByUserIdAndStatus(User userId, String status);

    // 특정 사용자가 작성한 게시글 중 삭제 여부와 상태로 필터링된 게시글 조회
    List<Bbs> findByUserIdAndDeletedAndStatus(User userId, int deleted, String status);

    // 특정 사용자와 삭제된 게시글 조회
    List<Bbs> findByUserIdAndDeleted(User userId, int deleted);
    
    // 사용자와 status 필터로 게시글 조회
    List<Bbs> findByUserIdAndStatusAndActiveTrue(User userId, String status);

    // status가 NULL이거나 'VISIBLE'인 게시글 조회 (NULL 방지)
    @Query("SELECT b FROM Bbs b WHERE b.status IS NULL OR b.status = 'VISIBLE'")
    List<Bbs> findVisibleOrNullStatusPosts();
    
    List<Bbs> findByUserId_Id(Long userId);
}
