package com.ictedu.search.favorite.repository;

import com.ictedu.search.favorite.model.entity.FavoriteCompany;
import com.ictedu.user.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FavoriteRepository extends JpaRepository<FavoriteCompany, Long> {
    FavoriteCompany findByUserAndCompanyId(User user, String companyId);
    List<FavoriteCompany> findByUser(User user);
}
