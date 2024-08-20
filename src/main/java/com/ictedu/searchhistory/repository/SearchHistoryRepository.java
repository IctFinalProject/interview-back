package com.ictedu.searchhistory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ictedu.searchhistory.model.entity.SearchHistory;
import com.ictedu.user.model.entity.User;

import java.util.List;

public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {
    List<SearchHistory> findByUserOrderBySearchedAtDesc(User user);
}
