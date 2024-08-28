package com.ictedu.bbs.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import com.ictedu.bbs.model.entity.FileBbs;
import com.ictedu.bbs.service.BbsDto;

@Repository
public interface FileBbsRepository extends JpaRepository<FileBbs, Long>{
	BbsDto save(BbsDto bbs);
	
}
