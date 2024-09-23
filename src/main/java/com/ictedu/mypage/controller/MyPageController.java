package com.ictedu.mypage.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ictedu.bbs.model.entity.Bbs;
import com.ictedu.bbs.model.entity.BbsComment;
import com.ictedu.bbs.service.BbsCommentService;
import com.ictedu.bbs.service.BbsService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class MyPageController {
	
	@Autowired
    private BbsCommentService bbsCommentService;
	
	@Autowired
    private BbsService bbsService;
	
	@PostMapping("/getMyBbsComment")
    public List<BbsComment> getMyBbsComment(@RequestBody Long userId) {
        return bbsCommentService.findByUserId(userId);
    }
	
	@PostMapping("/getMyBbsPost")
    public List<Bbs> getMyBbsPost(@RequestBody Long userId) {
        return bbsService.findByUserId(userId);
    }
}
