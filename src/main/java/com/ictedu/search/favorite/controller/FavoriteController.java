package com.ictedu.search.favorite.controller;

import com.ictedu.search.favorite.model.entity.FavoriteCompany;
import com.ictedu.search.favorite.service.FavoriteService;
import com.ictedu.user.model.entity.User;
import com.ictedu.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/favorite")
public class FavoriteController {

    private final UserRepository userRepository;
    private final FavoriteService favoriteService;

    @Autowired
    public FavoriteController(UserRepository userRepository, FavoriteService favoriteService) {
        this.userRepository = userRepository;
        this.favoriteService = favoriteService;
    }

    @PostMapping("/addFavorite")
    public ResponseEntity<Void> addFavorite(@RequestBody FavoriteCompany favoriteCompany) {

        Optional<User> optionalUser = userRepository.findByEmail(favoriteCompany.getUserEmail());
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            favoriteService.addFavoriteCompany(user, favoriteCompany);
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/removeFavorite")
    public ResponseEntity<Void> removeFavorite(@RequestBody FavoriteCompany favoriteCompany) {

        Optional<User> optionalUser = userRepository.findByEmail(favoriteCompany.getUserEmail());
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            favoriteService.removeFavoriteCompany(user, favoriteCompany);
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/getFavorites")
    public ResponseEntity<List<FavoriteCompany>> getFavorites(@RequestParam String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            List<FavoriteCompany> favorites = favoriteService.getFavoriteCompanies(user);
            return ResponseEntity.ok(favorites);
        }
        return ResponseEntity.badRequest().build();
    }
}
