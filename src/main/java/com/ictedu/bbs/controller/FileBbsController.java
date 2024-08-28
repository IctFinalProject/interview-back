package com.ictedu.bbs.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ictedu.bbs.model.entity.Bbs;
import com.ictedu.bbs.service.BbsService;
import com.ictedu.bbs.service.FileBbsService;

import io.jsonwebtoken.io.IOException;

@RestController
@RequestMapping("/files")
public class FileBbsController {
	/*
	@Autowired
	private BbsService bbsService;
	
	@PostMapping("/upload")
    public ResponseEntity<?> uploadFiles(
            @RequestParam("bbs") Bbs bbs,
            @RequestParam("files") List<MultipartFile> files) {

        try {
            // FileService를 사용하여 파일을 게시글에 첨부
            bbsService.saveFiles(bbs, files);
            return ResponseEntity.ok("Files uploaded successfully");
        } catch (IOException e) {
            // 파일 업로드 중 오류 발생 시, 에러 메시지 반환
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading files");
        } catch (Exception e) {
            // 기타 오류 발생 시, 에러 메시지 반환
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Bbs ID");
        }
    }
    */
}
