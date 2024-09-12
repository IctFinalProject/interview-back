package com.ictedu.adminpage.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ictedu.bbs.model.entity.BbsReport;

@Repository
public interface AdminReportedPostRepository extends JpaRepository<BbsReport, Long> {

	List<BbsReport> findAllByStatus(String status);
}
