package com.ictedu.adminpage.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ictedu.adminpage.service.AdminReportedService;
import com.ictedu.bbs.model.entity.BbsReport;

@RestController
@RequestMapping("/api/adminreported")
public class AdminReportedController {

    private final AdminReportedService adminReportedService;

    public AdminReportedController(AdminReportedService adminReportedService) {
        this.adminReportedService = adminReportedService;
    }

    // 모든 신고된 게시글 조회 API
    @GetMapping("/reportedposts")
    public ResponseEntity<List<Map<String, Object>>> getAllReportedPosts() {
        List<BbsReport> reportedPosts = adminReportedService.getAllReportedPosts();

     // 게시글에 대한 신고만 필터링
        List<Map<String, Object>> response = reportedPosts.stream()
            .filter(bbsReport -> bbsReport.getComment() == null) // 댓글 신고는 제외
            .map(bbsReport -> {
                Map<String, Object> map = new HashMap<>();
                map.put("reportId", bbsReport.getId());
                map.put("title", bbsReport.getBbs().getTitle());
                map.put("content", bbsReport.getBbs().getContent());
                map.put("username", bbsReport.getBbs().getUsername());
                map.put("reason", bbsReport.getReason());
                map.put("status", bbsReport.getStatus());
                map.put("reportedAt", bbsReport.getReportedAt().toString());
                map.put("reporterName", bbsReport.getReporter() != null ? bbsReport.getReporter().getUsername() : "Unknown");
                map.put("reporterId", bbsReport.getReporter() != null ? bbsReport.getReporter().getId() : null); // 추가한사람 윤지은
                map.put("deletedReason", bbsReport.getBbs().getDeletedReason());
                return map;
            }).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    // 특정 신고된 게시글 상세 조회
    @GetMapping("/reportedposts/{reportId}")
    public ResponseEntity<Map<String, Object>> getReportedPostDetails(@PathVariable Long reportId) {
        BbsReport bbsReport = adminReportedService.getReportedPostById(reportId);

        if (bbsReport == null) {
            return ResponseEntity.status(404).body(Map.of("error", "해당 게시글을 찾을 수 없습니다."));
        }

        // 신고된 게시글에 대한 상세 정보 반환
        Map<String, Object> response = new HashMap<>();
        response.put("reportId", bbsReport.getId());
        response.put("title", bbsReport.getBbs().getTitle());
        response.put("content", bbsReport.getBbs().getContent());
        response.put("username", bbsReport.getBbs().getUsername());
        response.put("reason", bbsReport.getReason());
        response.put("status", bbsReport.getStatus());
        response.put("reportedAt", bbsReport.getReportedAt().toString());
        response.put("createdAt", bbsReport.getBbs().getCreatedAt().toString()); // Bbs의 createdAt 사용
        response.put("reporterName", bbsReport.getReporter() != null ? bbsReport.getReporter().getUsername() : "Unknown");

        return ResponseEntity.ok(response);
    }

    // 게시글과 관련된 신고 및 댓글을 영구적으로 삭제하는 API
    @DeleteMapping("/delete/{reportId}")
    public ResponseEntity<Void> deletePostPermanently(@PathVariable Long reportId) {
        try {
            adminReportedService.deletePostPermanently(reportId);
            return ResponseEntity.ok().build(); // 성공적으로 삭제되었음을 응답
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(null); // 삭제하려는 신고번호가 존재하지 않을 경우 404 응답
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null); // 그 외 다른 오류 발생 시 500 응답
        }
    }

    // 신고 상태 업데이트 (String으로 상태 처리)
    @PutMapping("/updatestatus/{reportId}/{status}")
    public ResponseEntity<Void> updateReportStatus(@PathVariable Long reportId, @PathVariable String status) {
        try {
            adminReportedService.updateReportStatus(reportId, status);  // status를 String으로 받음
            return ResponseEntity.ok().build();  // 상태 업데이트 성공 시 응답
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(null);  // 해당 신고번호가 없을 경우 404 응답
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);  // 그 외 오류 발생 시 500 응답
        }
    }

    // 신고된 댓글 목록 조회 API
    @GetMapping("/reportedcomments")
    public ResponseEntity<List<Map<String, Object>>> getAllReportedComments() {
        List<BbsReport> reportedComments = adminReportedService.getAllReportedComments();

        List<Map<String, Object>> response = reportedComments.stream().map(bbsReport -> {
            Map<String, Object> map = new HashMap<>();
            map.put("reportId", bbsReport.getId());
            map.put("commentContent", bbsReport.getComment().getContent());
            map.put("username", bbsReport.getComment().getUser().getUsername());
            map.put("reason", bbsReport.getReason());
            map.put("title", bbsReport.getBbs().getTitle());
            map.put("bbsId", bbsReport.getBbs().getBbsId());
            map.put("status", bbsReport.getStatus());
            map.put("createdAt", bbsReport.getBbs().getCreatedAt().toString());
            map.put("reportedAt", bbsReport.getReportedAt().toString());
            map.put("reporterName", bbsReport.getReporter() != null ? bbsReport.getReporter().getUsername() : "Unknown");
            map.put("commentId", bbsReport.getComment().getCommentId());
            map.put("deletedReason", bbsReport.getComment().getDeletedReason());
            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    // 댓글 영구 삭제 API
    @DeleteMapping("/deletecomment/{commentId}")
    public ResponseEntity<Void> deleteCommentPermanently(@PathVariable Long commentId) {
        try {
            adminReportedService.deleteCommentPermanently(commentId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    // 댓글 복구 API 추가
    @PutMapping("/restorecomment/{commentId}")
    public ResponseEntity<Void> restoreComment(@PathVariable Long commentId) {
        try {
            adminReportedService.restoreComment(commentId);  // 댓글 복구 서비스 호출
            return ResponseEntity.ok().build();  // 복구 성공 시 응답
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(null);  // 해당 댓글이 없을 경우 404 응답
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);  // 그 외 오류 발생 시 500 응답
        }
    }
}
