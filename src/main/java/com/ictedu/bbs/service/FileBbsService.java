package com.ictedu.bbs.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ictedu.bbs.model.entity.Bbs;
import com.ictedu.bbs.model.entity.FileBbs;
import com.ictedu.bbs.repository.FileBbsRepository;

@Service
public class FileBbsService {

	@Autowired
	private FileBbsRepository fileBbsRepository;
	
	/*
	public void saveFileBbs(FileBbs fileBbs) {
		fileBbsRepository.save(fileBbs);
	}
	*/
	/*
	public void saveFiles(Long bbs, List<MultipartFile> files) {
		// 주어진 게시글 ID로 게시글을 조회
	    Bbs bbs = bbsRepository.findById(bbsId)
	            .orElseThrow(() -> new IllegalArgumentException("Invalid Bbs ID"));

	    // 각 파일에 대해 처리
	    for (MultipartFile file : files) {
	        // 새로운 FileBbsEntity 객체를 생성하여 파일 정보 설정
	        FileBbsEntity fileBbs = new FileBbsEntity();
	        fileBbs.setFileName(file.getOriginalFilename());  // 원본 파일명 설정
	        fileBbs.setFileType(file.getContentType());       // 파일 MIME 타입 설정
	        fileBbs.setData(file.getBytes());                 // 파일 데이터를 바이트 배열로 설정
	        fileBbs.setBbs(bbs);                              // 파일을 게시글과 연관
	        
	        // 파일을 데이터베이스에 저장
	        fileBbsRepository.save(fileBbs);
	    }
		
	}*/
}
