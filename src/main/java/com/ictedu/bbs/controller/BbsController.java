package com.ictedu.bbs.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.ictedu.bbs.model.entity.Bbs;
import com.ictedu.bbs.service.BbsDto;
import com.ictedu.bbs.service.BbsService;
import com.ictedu.user.model.entity.User;
import com.ictedu.user.service.UserService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/bbs")
@CrossOrigin(origins = "http://localhost:3000")
public class BbsController {

    @Autowired
    private BbsService bbsService;
    
    @Autowired
    private UserService userService;
    
    //게시글 목록 조회
    @GetMapping
    public List<Bbs> getAllBbs() {
        return bbsService.findAll();
    }
    
    //게시글 단건 조회
    @GetMapping("/{id}")
    public ResponseEntity<Bbs> getBbsById(@PathVariable("id") Long id) {
        return bbsService.findById(id)
                .map(bbs -> ResponseEntity.ok().body(bbs))
                .orElse(ResponseEntity.notFound().build());
    }
    /*
    // 게시글 첨부파일 조회
    @GetMapping("/{id}/files/{fileIndex}")
    public ResponseEntity<byte[]> getFile(@PathVariable("id") Long id,
                                           @PathVariable("fileIndex") int fileIndex) {
        byte[] file = bbsService.getFile(id, fileIndex);
        if (file == null) {
            return ResponseEntity.notFound().build();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "file" + fileIndex);
        return new ResponseEntity<>(file, headers, HttpStatus.OK);
    }
    */
    @PostMapping
    public ResponseEntity<?> createBbs(@RequestParam("title") String title,
                                       @RequestParam("content") String content,
                                       @RequestParam("id") String userId,
                                       @RequestParam("files") List<MultipartFile> files) throws IOException {
        Optional<User> userOptional = userService.findById(userId);
        if (!userOptional.isPresent()) {
            return ResponseEntity.badRequest().body("Invalid user ID");
        }

        User user = userOptional.get();
        BbsDto bbsDto = new BbsDto(title, content, user);
        Bbs newBbs = bbsService.insertBbs(bbsDto, files);

        return ResponseEntity.ok(newBbs);
    }
    
    /*
    //게시글 수정
    @PutMapping("/{id}")
    public ResponseEntity<Bbs> updateBbs(@PathVariable("id") Long id, @RequestBody Bbs bbsDetails) {
        return bbsService.findById(id)
                .map(bbs -> {
                    bbs.setTitle(bbsDetails.getTitle());
                    bbs.setContent(bbsDetails.getContent());
                    // 기타 필드 업데이트
                    Bbs updatedBbs = bbsService.update(bbs);
                    return ResponseEntity.ok().body(updatedBbs);
                })
                .orElse(ResponseEntity.notFound().build());
    }*/
    @PutMapping("/{id}")
    public ResponseEntity<?> updateBbs(@PathVariable("id") Long id, @RequestParam("userId") String userId, @RequestBody Bbs bbsDetails) {
        return bbsService.findById(id)
                .map(bbs -> {
                    // 게시글 작성자와 요청한 사용자가 일치하는지 확인
                    if (!bbs.getUserId().getId().equals(userId)) {
                        return ResponseEntity.status(403).body("You are not authorized to edit this post.");
                    }
                    
                    // 일치하는 경우 수정 작업 수행
                    bbs.setTitle(bbsDetails.getTitle());
                    bbs.setContent(bbsDetails.getContent());
                    // 기타 필드 업데이트
                    Bbs updatedBbs = bbsService.update(bbs);
                    return ResponseEntity.ok().body(updatedBbs);
                })
                .orElse(ResponseEntity.notFound().build());
    }
    /*
    //게시글 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteBbs(@PathVariable("id") Long id) {
        return bbsService.findById(id)
                .map(bbs -> {
                    bbsService.deleteById(id);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
    */
    @DeleteMapping("/{id}")
    public ResponseEntity<? extends Object> deleteBbs(@PathVariable("id") Long id, @RequestParam("userId") String userId) {
        return bbsService.findById(id)
                .map(bbs -> {
                    // 게시글 작성자와 요청한 사용자가 일치하는지 확인
                    if (!bbs.getUserId().getId().equals(userId)) {
                        return ResponseEntity.status(403).body("You are not authorized to delete this post.");
                    }
                    
                    // 일치하는 경우 삭제 작업 수행
                    bbsService.deleteById(id);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
