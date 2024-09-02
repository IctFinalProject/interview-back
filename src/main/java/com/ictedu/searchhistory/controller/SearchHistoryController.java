package com.ictedu.searchhistory.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import com.ictedu.searchhistory.model.entity.SearchHistory;
import com.ictedu.searchhistory.service.SearchHistoryService;
import com.ictedu.user.model.entity.User;
import com.ictedu.user.repository.UserRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

//localhost:8080/api/search/save-search-history
@RestController
@RequestMapping("/api/search")
public class SearchHistoryController {
	 private final UserRepository userRepository;
	 
	 public SearchHistoryController(UserRepository userRepository) {
	        this.userRepository = userRepository;
	  }
    @Autowired
    private  SearchHistoryService searchHistoryService;
    
    // 검색 기록 저장 처리
    @PostMapping("/saveSearchHistory")
    public ResponseEntity<Void> saveSearchHistory(@RequestParam Map<String, String> request, @RequestParam String email) {
    	if(!(request == null)) {
    		searchHistoryService.saveSearchHistory(request.get("searchInput"), email);
        	return ResponseEntity.ok().build();
    	}
    	return ResponseEntity.badRequest().build(); // 수정: 잘못된 요청 처리
    }
    
    // 검색 기록 삭제 처리
    @DeleteMapping("/deleteSearchHistory")
    public ResponseEntity<Void> deleteSearchHistory(@RequestParam String term, @RequestParam String email) {
        searchHistoryService.deleteSearchHistory(term, email);
        return ResponseEntity.ok().build();
    }

   
    // 검색 기록 조회 및 JSON 응답
    @GetMapping("/searchHistory")
    public ResponseEntity<List<String>> getSearchHistory(@RequestParam String email) {
    	Optional<User> optionalUser = userRepository.findByEmail(email);
        User user = optionalUser.get();
        List<SearchHistory> searchHistories = searchHistoryService.getSearchHistory(user);
        
        // 검색어만 추출하여 반환
        List<String> searchTerms = searchHistories.stream()
            .flatMap(history -> history.getSearchTerms().stream())
            .distinct()
            .collect(Collectors.toList());
        
        // 추가: 검색어 목록이 비어있을 경우 로그 출력
        if (searchTerms.isEmpty()) {
            System.out.println("No search terms found for user: " + user.getEmail());
        }
        
        // 반환할 검색 기록을 출력
//        System.out.println("Returning search terms: " + searchTerms);
        
        return ResponseEntity.ok(searchTerms);
    }
}

