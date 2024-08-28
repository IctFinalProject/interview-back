/*
package com.ictedu.spring.controller.bbs;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.ictedu.bbs.model.entity.Bbs;
import com.ictedu.bbs.model.entity.QnA;
import com.ictedu.spring.service.bbs.BbsDto;
import com.ictedu.spring.service.bbs.BbsService;
import com.ictedu.spring.service.bbs.QnaDto;
import com.ictedu.spring.service.bbs.QnaService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
/*
@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "http://localhost:3000")
public class QnaController {

    @Autowired
    private QnaService qnaService;
    
    //QnA 목록 조회
    @GetMapping("/qna")
    public List<QnA> getAllBbs() {
        return qnaService.findAll();
    }
    //게시글 단건 조회
    @GetMapping("/{id}")
    public ResponseEntity<QnA> getBbsById(@PathVariable Long id) {
        return qnaService.findById(id)
                .map(qna -> ResponseEntity.ok().body(qna))
                .orElse(ResponseEntity.notFound().build());
    }
    //답변 등록
    @PostMapping("/insert")
    public ResponseEntity<?> createQna( @RequestParam("answer") String answer) throws IOException {
    	
    	System.out.println("answer: "+answer);
    	
    	 
    	
    	QnaDto qnaDto = new QnaDto(answer);
    	QnA newQnA = qnaService.insertQna(qnaDto);
    	
    	
 
    	System.out.println("newQnA: "+newQnA);
        return ResponseEntity.ok(qnaDto);
    }
  
    //게시글 수정
    @PutMapping("/{id}")
    public ResponseEntity<QnA> updateBbs(@PathVariable Long id, @RequestBody QnA qnaDetails) {
        return qnaService.findById(id)
                .map(qna -> {
                    qna.setTitle(qnaDetails.getTitle());
                    qna.setContent(qnaDetails.getContent());
                    // 기타 필드 업데이트
                    QnA updatedQnA = qnaService.update(qna);
                    return ResponseEntity.ok().body(updatedQnA);
                })
                .orElse(ResponseEntity.notFound().build());
    }
    //게시글 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteQnA(@PathVariable Long id) {
        return qnaService.findById(id)
                .map(bbs -> {
                	qnaService.deleteById(id);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
*/
