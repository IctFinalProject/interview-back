package com.ictedu.search.favorite.service;

import com.ictedu.search.favorite.model.entity.FavoriteCompany;
import com.ictedu.search.favorite.repository.FavoriteRepository;
import com.ictedu.user.model.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;

    @Autowired
    public FavoriteService(FavoriteRepository favoriteRepository) {
        this.favoriteRepository = favoriteRepository;
    }

    public void addFavoriteCompany(User user, FavoriteCompany favoriteCompany) {
        favoriteCompany.setUser(user);
        favoriteRepository.save(favoriteCompany);
    }

    public void removeFavoriteCompany(User user, FavoriteCompany favoriteCompany) {
        FavoriteCompany favoriteToRemove = favoriteRepository.findByUserAndCompanyId(user, favoriteCompany.getCompanyId());
        
        if (favoriteToRemove != null) {
            favoriteRepository.delete(favoriteToRemove);
        }
    }

    public List<FavoriteCompany> getFavoriteCompanies(User user) {
        return favoriteRepository.findByUser(user);
    }
}
