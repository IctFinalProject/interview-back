package com.ictedu.search.favorite.service;

import com.ictedu.search.favorite.model.entity.FavoriteCompany;
import com.ictedu.search.favorite.repository.FavoriteRepository;
import com.ictedu.user.model.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;

    @Autowired
    public FavoriteService(FavoriteRepository favoriteRepository) {
        this.favoriteRepository = favoriteRepository;
    }

    public void addFavoriteCompany(User user, FavoriteCompany favoriteCompany) {
        System.out.println("Adding favorite company: " + favoriteCompany + " for user: " + user.getEmail());
        favoriteCompany.setUser(user);
        favoriteRepository.save(favoriteCompany);
        System.out.println("Favorite company saved.");
    }

    public void removeFavoriteCompany(User user, FavoriteCompany favoriteCompany) {
        System.out.println("Removing favorite company: " + favoriteCompany + " for user: " + user.getEmail());
        FavoriteCompany favoriteToRemove = favoriteRepository
            .findByUserAndCompanyId(user, favoriteCompany.getCompanyId());
        
        if (favoriteToRemove != null) {
            favoriteRepository.delete(favoriteToRemove);
            System.out.println("Favorite company removed.");
        } else {
            System.out.println("Favorite company not found, nothing to remove.");
        }
    }
}
