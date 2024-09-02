package com.ictedu.adminpage.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ictedu.adminpage.model.AdminNoticeModel;

public interface AdminNoticeRepository extends JpaRepository<AdminNoticeModel,Long> {

}
