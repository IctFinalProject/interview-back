package com.ictedu.adminpage.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ictedu.adminpage.model.CompanyNoticeModel;

public interface CompanyNoticeRepository extends JpaRepository<CompanyNoticeModel,Long> {

}
