package com.ictedu.bbs.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ictedu.bbs.model.entity.Bbs;
import com.ictedu.bbs.model.entity.BbsComment;
import com.ictedu.bbs.service.BbsCommentService;
import com.ictedu.bbs.service.BbsDto;
import com.ictedu.bbs.service.BbsReportService;
import com.ictedu.bbs.service.BbsService;
import com.ictedu.bbs.service.CommentRequestDto;
import com.ictedu.bbs.service.CommentRequestDto;
import com.ictedu.bbs.service.ReportRequestDto;
import com.ictedu.user.model.entity.User;
import com.ictedu.user.service.UserService;


import java.io.IOException;
import java.time.LocalDateTime;
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
    private BbsCommentService bbsCommentService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private BbsReportService bbsReportService;  // BbsReportService 주입
    
    //게시글 목록 조회
    @GetMapping
    public List<Bbs> getAllBbs() {
        List<Bbs> bbsList = bbsService.findAllByDeleted(0);  // 삭제되지 않은 글만 조회
        return bbsList;
    }
    
    //삭제된 게시글 목록 조회(deleted = 1인 게시글만)
    @GetMapping("/deleted")
    public List<Bbs> getDeletedBbs(){
    	List<Bbs> deletedBbsList = bbsService.findAllDeletedPosts(); //삭제된 글만 조회
    	return deletedBbsList;
    }
    
    //삭제된 댓글만 조회
    @GetMapping("/comments/deleted")
    public List<BbsComment> getDeletedComments(){
    	return bbsCommentService.findAllDeletedComments();//삭제된 댓글만 반환
    }
    
    // 게시글 단건 조회 (GET 요청)(원본)
   
    @GetMapping("/{id}")
    public ResponseEntity<Bbs> getBbsById(
        @PathVariable("id") Long id,
        @RequestParam Map<String, String> params){
        // 쿼리 파라미터 로그
        System.out.println("Query Params: " + params);

        boolean increment = Boolean.parseBoolean(params.getOrDefault("increment", "true"));
        System.out.println("Parsed increment value: " + increment);

        Optional<Bbs> bbsOptional = bbsService.findById(id);
        if (bbsOptional.isPresent()) {
            Bbs bbs = bbsOptional.get();
            if (increment) {
                bbsService.incrementHitcount(id); // 조회수 증가
            }
            
        
            return ResponseEntity.ok().body(bbs);
        } else {
        	 return ResponseEntity.notFound().build();
        }
    }
    
   
   
    
    // 게시글 단건 조회 (POST 요청)
    @PostMapping("/search")
    public ResponseEntity<Bbs> getBbsByIdPost(@RequestBody BbsDto bbsDto) {
        Long id = bbsDto.getBbsId();  // bbsDto 인스턴스를 통해 호출
        System.out.println("POST 요청: 게시글 ID: " + id); // 한글 콘솔 체크
        Optional<Bbs> bbsOptional = bbsService.findById(id);
        
        if (bbsOptional.isPresent()) {
            Bbs bbs = bbsOptional.get();
            return ResponseEntity.ok().body(bbs);
        } else {
            System.out.println("게시글 ID " + id + " 못 찾음"); // 한글 콘솔 체크
            return ResponseEntity.notFound().build();
        }
    }

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

    // 파일 다운로드
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

    // 게시글 등록
    @PostMapping
    public ResponseEntity<?> createBbs(@RequestParam("title") String title,
                                       @RequestParam("content") String content,
                                       @RequestParam("id") String userId,
                                       @RequestParam(value="files", required = false) List<MultipartFile> files) throws IOException {
        Optional<User> userOptional = userService.findById(userId);
        if (!userOptional.isPresent()) {
            System.out.println("잘못된 사용자 ID: " + userId); // 한글 콘솔 체크
            return ResponseEntity.badRequest().body("Invalid user ID");
        }

        User user = userOptional.get();
        BbsDto bbsDto = new BbsDto(title, content, user);
        Bbs newBbs = bbsService.insertBbs(bbsDto, files);
//        System.out.println("새 게시글 생성됨: " + newBbs); // 한글 콘솔 체크

        return ResponseEntity.ok(newBbs);
    }
    
//    // 뮤츠: 게시글 수정 (파일 및 내용 함께 수정)
//    @PostMapping("/{id}")
//    public ResponseEntity<Bbs> updateBbs(
//        @PathVariable("id") Long id,
//        @RequestParam("title") String title,
//        @RequestParam("content") String content,
//        @RequestParam(value = "files", required = false) List<MultipartFile> files,  // 새 파일 (없을 수도 있음)
//        @RequestParam(value = "filesToRemove", required = false) List<String> filesToRemove) throws IOException {  // 삭제할 파일
//
//        Bbs bbs = bbsService.findById(id).orElseThrow(() -> new RuntimeException("게시글 못 찾음"));
//
//        // 기존 파일에서 삭제할 파일 제거
//        Map<String, byte[]> fileMap = bbs.getFiles();
//        if (filesToRemove != null) {
//            for (String fileName : filesToRemove) {
//                fileMap.remove(fileName); // 삭제할 파일 제거
//            }
//        }
//
//        // 새로 추가된 파일 처리
//        if (files != null && !files.isEmpty()) {
//            for (MultipartFile file : files) {
//                fileMap.put(file.getOriginalFilename(), file.getBytes()); // 새 파일 추가
//            }
//        }
//
//        // 게시글 내용 수정
//        bbs.setTitle(title);
//        bbs.setContent(content);
//        bbs.setFiles(fileMap);
//
//        Bbs updatedBbs = bbsService.update(bbs);
//        return ResponseEntity.ok(updatedBbs);
//    }
//
//
//    // 뮤츠 끝
    //게시글 수정
    @PostMapping("/{id}")
    public ResponseEntity<Bbs> updateBbs(
        @PathVariable("id") Long id,
        @RequestParam("title") String title,
        @RequestParam("content") String content,
        @RequestParam(value = "files", required = false) List<MultipartFile> files,  // 새 파일
        @RequestParam(value = "filesToRemove", required = false) String filesToRemoveJson) throws IOException {

        System.out.println("Received filesToRemove JSON: " + filesToRemoveJson); // 콘솔 로그 추가

        // filesToRemove JSON을 리스트로 변환
        List<String> filesToRemove = new ObjectMapper().readValue(filesToRemoveJson, new TypeReference<List<String>>() {});

        System.out.println("Files to remove list: " + filesToRemove); // 파일 목록 출력

        Bbs bbs = bbsService.findById(id).orElseThrow(() -> new RuntimeException("게시글 못 찾음"));

        // 기존 파일에서 삭제할 파일 제거
        Map<String, byte[]> fileMap = bbs.getFiles();
        if (filesToRemove != null && !filesToRemove.isEmpty()) {
            for (String fileName : filesToRemove) {
                System.out.println("Removing file: " + fileName); // 삭제할 파일 출력
                fileMap.remove(fileName);  // 삭제할 파일 제거
            }
        }

        // 새로 추가된 파일 처리
        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                fileMap.put(file.getOriginalFilename(), file.getBytes());
            }
        }

        // 게시글 내용 수정
        bbs.setTitle(title);
        bbs.setContent(content);
        bbs.setFiles(fileMap);
        bbs.setEdited(1);  // 수정 여부를 1로 설정
        bbs.setEdited_date(LocalDateTime.now());  // 수정 날짜 기록

        Bbs updatedBbs = bbsService.update(bbs);
        return ResponseEntity.ok(updatedBbs);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBbs(@PathVariable("id") Long id, @RequestParam("userId") String userId) {
        System.out.println("게시글 삭제 요청, ID: " + id + ", 사용자 ID: " + userId); // 사용자 ID와 게시글 ID 출력
        
        Optional<Bbs> bbsOptional = bbsService.findById(id);
        if (bbsOptional.isPresent()) {
            Bbs bbs = bbsOptional.get();
//            System.out.println("게시글 찾음: " + bbs.getBbsId() + ", 작성자: " + bbs.getUserId().getId()); // 게시글 정보 출력
            
            if (!bbs.getUserId().getId().equals(Long.valueOf(userId))) {
                System.out.println("사용자 ID 불일치: 권한 없음"); // 권한 불일치 시 로그 출력
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to delete this post.");
            }
            
            // 게시글 소프트 삭제 처리
            System.out.println("게시글 삭제 처리 중...");
            bbsService.deleteBbs(id, false);  // 게시글 삭제 (일반 삭제)

            System.out.println("게시글 삭제 완료: " + bbs.getBbsId());
            return ResponseEntity.ok().build();
        } else {
            System.out.println("게시글 ID 못 찾음: " + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Post not found");
        }
    }
    
    @PostMapping("/report")
    public ResponseEntity<String> reportPostOrComment(@RequestBody ReportRequestDto request) {
    	 // 전달된 userId를 로그로 확인
        System.out.println("Received userId: " + request.getUserId());
        // 신고자를 찾음
        User reporter = userService.findById(request.getUserId())
            .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 게시물 신고 처리
        if (request.getPostId() != null) {
            Optional<Bbs> bbsOptional = bbsService.findById(request.getPostId());
            if (bbsOptional.isPresent()) {
                Bbs bbs = bbsOptional.get();
                bbsReportService.saveReport(bbs, reporter, request.getReason(), request.getAdditionalInfo());
                return ResponseEntity.ok("게시글 신고가 접수되었습니다.");
            } else {
                return ResponseEntity.status(404).body("게시글을 찾을 수 없습니다.");
            }
        }

        // 댓글 신고 처리
        if (request.getCommentId() != null) {
            Optional<BbsComment> commentOptional = bbsCommentService.findById(request.getCommentId());
            if (commentOptional.isPresent()) {
                BbsComment comment = commentOptional.get();
                Bbs bbs = comment.getBbs();
                bbsReportService.saveCommentReport(bbs, comment, reporter, request.getReason(), request.getAdditionalInfo());
                return ResponseEntity.ok("댓글 신고가 접수되었습니다.");
            } else {
                return ResponseEntity.status(404).body("댓글을 찾을 수 없습니다.");
            }
        }

        return ResponseEntity.status(400).body("신고할 대상이 없습니다.");
    }




    // 댓글 목록 조회
    @GetMapping("/{bbsId}/comments")
    public List<BbsComment> getComments(@PathVariable("bbsId") Long bbsId) {
        return bbsCommentService.getCommentsByBbsId(bbsId);
    }

    // 댓글 생성
    @PostMapping("/{bbsId}/comments")
    public BbsComment createComment(@PathVariable("bbsId") Long bbsId, @RequestBody CommentRequestDto request) {
        Bbs bbs = bbsService.findById(bbsId).orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        String changedId = String.valueOf(request.getUserId());
        User user = userService.findById(changedId).orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        return bbsCommentService.createComment(bbs, request.getContent(), user);
    }

    // 댓글 수정
    @PutMapping("/comments/{commentId}")
    public BbsComment updateComment(@PathVariable("commentId") Long commentId, @RequestBody CommentRequestDto request) {
        return bbsCommentService.updateComment(commentId, request.getContent());
    }
    
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<String> deleteComment(@PathVariable("commentId") Long commentId) {
        bbsCommentService.deleteComment(commentId);
        return ResponseEntity.ok("댓글이 삭제되었습니다.");
    }
    



}