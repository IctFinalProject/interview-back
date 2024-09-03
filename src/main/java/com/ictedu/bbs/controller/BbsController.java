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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/bbs")
@CrossOrigin(origins = "http://localhost:3000")
public class BbsController {

    @Autowired
    private BbsService bbsService;
    
    @Autowired
    private UserService userService;
    
    // 게시글 목록 조회
    @GetMapping
    public List<Bbs> getAllBbs() {
        return bbsService.findAll();
    }
    
    // 게시글 단건 조회 (GET 요청)
    @GetMapping("/{id}")
    public ResponseEntity<Bbs> getBbsById(@PathVariable("id") String id) {
        System.out.println("GET Request: Fetching Bbs with ID: " + id);  // 콘솔 로그 추가
        Optional<Bbs> bbsOptional = bbsService.findById(Long.parseLong(id));
        
        if (bbsOptional.isPresent()) {
            Bbs bbs = bbsOptional.get();
            System.out.println("Bbs found: " + bbs);  // 콘솔 로그 추가
            return ResponseEntity.ok().body(bbs);
        } else {
            System.out.println("Bbs with ID " + id + " not found");  // 콘솔 로그 추가
            return ResponseEntity.notFound().build();
        }
    }

    // 게시글 단건 조회 (POST 요청)
    @PostMapping("/search")
    public ResponseEntity<Bbs> getBbsByIdPost(@RequestBody BbsDto bbsDto) {
        Long id = bbsDto.getId();
        System.out.println("POST Request: Fetching Bbs with ID: " + id);  // 콘솔 로그 추가
        Optional<Bbs> bbsOptional = bbsService.findById(id);
        
        if (bbsOptional.isPresent()) {
            Bbs bbs = bbsOptional.get();
            System.out.println("Bbs found: " + bbs);  // 콘솔 로그 추가
            return ResponseEntity.ok().body(bbs);
        } else {
            System.out.println("Bbs with ID " + id + " not found");  // 콘솔 로그 추가
            return ResponseEntity.notFound().build();
        }
    }
    /*
    // 게시글 첨부파일 조회
    @GetMapping("/{id}/files/{fileIndex}")
    public ResponseEntity<byte[]> getFile(@PathVariable("id") Long id,
                                           @PathVariable("fileIndex") int fileIndex) {
        System.out.println("Fetching file with Bbs ID: " + id + " and file index: " + fileIndex);  // 콘솔 로그 추가
        byte[] file = bbsService.getFile(id, fileIndex);
        if (file == null) {
            System.out.println("File not found for Bbs ID: " + id + " at index: " + fileIndex);  // 콘솔 로그 추가
            return ResponseEntity.notFound().build();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "file" + fileIndex);
        return new ResponseEntity<>(file, headers, HttpStatus.OK);
    }
    */
 // 게시글의 모든 파일 목록 조회
    @GetMapping("/{id}/files")
    public ResponseEntity<Map<String, String>> getFilesByBbsId(@PathVariable("id") Long id) {
        Map<String, byte[]> filesMap = bbsService.getFilesByBbsId(id);
        if (filesMap.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        
        // 파일 이름만 반환
        Map<String, String> fileNamesMap = new HashMap<>();
        filesMap.forEach((fileName, fileData) -> fileNamesMap.put(fileName, fileName));

        return ResponseEntity.ok(fileNamesMap);
    }

    @GetMapping("/{id}/files/{fileName}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable("id") Long id,
                                                @PathVariable("fileName") String fileName) {
        byte[] fileData = bbsService.getFile(id, fileName);
        if (fileData != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", fileName);
            return new ResponseEntity<>(fileData, headers, HttpStatus.OK);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    //게시글 등록
    @PostMapping
    public ResponseEntity<?> createBbs(@RequestParam("title") String title,
                                       @RequestParam("content") String content,
                                       @RequestParam("id") String userId,
                                       @RequestParam("files") List<MultipartFile> files) throws IOException {
        Optional<User> userOptional = userService.findById(userId);
        if (!userOptional.isPresent()) {
            System.out.println("Invalid user ID: " + userId);  // 콘솔 로그 추가
            return ResponseEntity.badRequest().body("Invalid user ID");
        }

        User user = userOptional.get();
        BbsDto bbsDto = new BbsDto(title, content, user);
        Bbs newBbs = bbsService.insertBbs(bbsDto, files);
        System.out.println("New Bbs created: " + newBbs);  // 콘솔 로그 추가

        return ResponseEntity.ok(newBbs);
    }
    
    // 게시글 수정
    @PutMapping("/{id}")
    public ResponseEntity<Bbs> updateBbs(@PathVariable("id") Long id, @RequestBody Bbs bbsDetails) {
        System.out.println("Updating Bbs with ID: " + id);  // 콘솔 로그 추가
        return bbsService.findById(id)
                .map(bbs -> {
                    bbs.setTitle(bbsDetails.getTitle());
                    bbs.setContent(bbsDetails.getContent());
                    Bbs updatedBbs = bbsService.update(bbs);
                    System.out.println("Bbs updated: " + updatedBbs);  // 콘솔 로그 추가
                    return ResponseEntity.ok().body(updatedBbs);
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    // 게시글 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBbs(@PathVariable("id") Long id, @RequestParam("userId") String userId) {
        System.out.println("Attempting to delete Bbs with ID: " + id + " by user: " + userId);  // 콘솔 로그 추가
        return bbsService.findById(id)
                .map(bbs -> {
                    if (!bbs.getUserId().getId().equals(userId)) {
                        System.out.println("User ID mismatch: Unauthorized deletion attempt");  // 콘솔 로그 추가
                        return ResponseEntity.status(403).body("You are not authorized to delete this post.");
                    }
                    bbsService.deleteById(id);
                    System.out.println("Bbs deleted with ID: " + id);  // 콘솔 로그 추가
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
