package com.ictedu.adminpage.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ictedu.adminpage.model.NoticeModel;

public interface NoticeRepository extends JpaRepository<NoticeModel,Long> {

}
