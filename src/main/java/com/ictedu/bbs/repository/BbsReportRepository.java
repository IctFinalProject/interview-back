package com.ictedu.bbs.repository;

import com.ictedu.bbs.model.entity.BbsReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BbsReportRepository extends JpaRepository<BbsReport, Long> {
}
