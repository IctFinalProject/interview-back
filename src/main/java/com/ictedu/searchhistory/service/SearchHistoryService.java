package com.ictedu.searchhistory.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Service;

import com.ictedu.searchhistory.model.entity.SearchHistory;

import com.ictedu.searchhistory.repository.SearchHistoryRepository;
import com.ictedu.security.jwt.util.JWTUtil;
import com.ictedu.security.service.RefreshService;
import com.ictedu.user.model.entity.User;
import com.ictedu.user.repository.UserRepository;
import com.ictedu.user.service.UserService;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Service
public class SearchHistoryService {
	 private final UserRepository userRepository;
	 
	 public SearchHistoryService(UserRepository userRepository) {
	        this.userRepository = userRepository;
	  }
	 	@Autowired
	    private SearchHistoryRepository searchHistoryRepository;

	    // 검색 기록 저장 메소드
	    public void saveSearchHistory(String searchInput, String email) {
//	        System.out.println("Search Input: " + searchInput);  // 입력된 검색어 로그
	        
	        List<String> searchTerms = Arrays.asList(searchInput.split("\\s+"));
//	        System.out.println("Search Terms: " + searchTerms);  // 분리된 검색어 로그
	        Optional<User> optionalUser = userRepository.findByEmail(email);
	        User user = optionalUser.get();
	        SearchHistory searchHistory = SearchHistory.builder()
	                .user(user)
	                .userEmail(user.getEmail())
	                .searchTerms(searchTerms)
	                .build();
	        
//	        System.out.println("SearchHistory entity: " + searchHistory);  // 엔터티 로그

	        searchHistoryRepository.save(searchHistory);
	        System.out.println("SearchHistory saved.");  // 저장 완료 로그
	    }

	 // 특정 유저의 검색 기록 조회 메소드
	    public List<SearchHistory> getSearchHistory(User user) {
	        List<SearchHistory> histories = searchHistoryRepository.findByUserOrderBySearchedAtDesc(user);
//	        System.out.println("Search histories for user " + user.getEmail() + ": " + histories);
	        
	        // 추가: 검색 기록이 없을 경우 빈 배열 반환
	        if (histories.isEmpty()) {
	            System.out.println("No search history found for user: " + user.getEmail());
	        }
	        
	        return histories;
	    }

	 // 검색 기록 삭제 메소드
	    public void deleteSearchHistory(String term, String email) {
	        Optional<User> optionalUser = userRepository.findByEmail(email);
	        if (optionalUser.isPresent()) {
	            User user = optionalUser.get();
	            List<SearchHistory> histories = searchHistoryRepository.findByUserOrderBySearchedAtDesc(user);

	            for (SearchHistory history : histories) {
	                Iterator<String> iterator = history.getSearchTerms().iterator();
	                while (iterator.hasNext()) {
	                    String searchTerm = iterator.next();
	                    if (searchTerm.equals(term)) {
	                        iterator.remove();
	                        if (history.getSearchTerms().isEmpty()) {
	                            searchHistoryRepository.delete(history);
	                        } else {
	                            searchHistoryRepository.save(history);
	                        }
	                        break;
	                    }
	                }
	            }
	        }
	    }
	}
	