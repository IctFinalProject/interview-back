package com.ictedu.user.controller;

import java.io.IOException;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ictedu.user.model.entity.User;
import com.ictedu.user.service.InputUser;
import com.ictedu.user.service.UserService;

import lombok.extern.slf4j.Slf4j;

@CrossOrigin(origins = "http://localhost:3000") // CORS 설정 추가
@RestController
@RequestMapping("/api/auth")
@Slf4j
public class UserController {

    private final UserService userService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserController(BCryptPasswordEncoder bCryptPasswordEncoder, UserService userService) {
    	this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userService = userService;
    }
    
    @PostMapping("/user")
    public ResponseEntity<?> signUpUser(
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("username") String username,
            @RequestParam("phone") String phone,
            @RequestParam("address") String address,
            @RequestParam("gender") String gender,
            @RequestParam("birth") String birth,
            @RequestParam(value = "profileImage", required = false) MultipartFile profileImage) {
        try {
            InputUser inputUser = new InputUser(
                    email, 
                    password, 
                    username, 
                    phone, 
                    address, 
                    gender, 
                    birth, 
                    profileImage != null ? profileImage.getBytes() : null);
            User newUser = userService.signUpUser(inputUser);
            return ResponseEntity.ok(newUser);
        } catch (IOException e) {
            return ResponseEntity.status(514).body("프로필 사진에서 오류가 발생하였습니다.");
        }
    }
    
    @GetMapping("/find-email")
    public ResponseEntity<?> findEmailByUsernameAndPhone(
            @RequestParam("username") String username, 
            @RequestParam("phone") String phone) {
        Optional<User> user = userService.findEmailByUsernameAndPhone(username, phone);
        System.out.println("user: "+user);
        System.out.println("username: "+username);
        System.out.println("phone: "+phone);
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get().getEmail());
        } else {
            return ResponseEntity.status(404).body("사용자를 찾을 수 없습니다.");
        }
    }
    
    @GetMapping("/verify-user")
    public ResponseEntity<?> verifyUserByUsernameAndEmail(
            @RequestParam("username") String username, 
            @RequestParam("email") String email) {
        Optional<User> user = userService.verifyUserByUsernameAndEmail(username, email);
        System.out.println("user: "+ user);
        System.out.println("username: "+ username);
        System.out.println("email: "+ email);
        if (user.isPresent()) {
        	return ResponseEntity.ok(user.get().getEmail());
        } else {
            return ResponseEntity.status(404).body("사용자를 찾을 수 없습니다.");
        }
    }
    
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String email = request.get("email");
        String newPassword = request.get("newPassword");
        
        Optional<User> user = userService.verifyUserByUsernameAndEmail(username, email);
        String beforePassword = user.get().getPassword();
        if(bCryptPasswordEncoder.matches(newPassword, beforePassword)) {
        	System.out.println("이전 비밀번호랑 일치!");
        	return ResponseEntity.status(512).body("이전 비밀번호는 사용할 수 없습니다.");
        }
        System.out.println("password1: " + beforePassword);
        System.out.println("username2: " + username);
        System.out.println("email2: " + email);
        System.out.println("newPassword: " + newPassword);
        
        boolean isUpdated = userService.updatePassword(username, email, newPassword);
        System.out.println("isUpdated: " + isUpdated);
        
        if (isUpdated) {
            return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
        } else {
            return ResponseEntity.status(513).body("비밀번호 변경에 실패하였습니다.");
        }
    }
    
    @GetMapping("/profile-image")
    public ResponseEntity<?> getProfileImage(@RequestParam("email") String email) {
        Optional<User> user = userService.findByEmail(email);
        
        if (user.isPresent() && user.get().getProfileImage() != null) {
            byte[] profileImage = user.get().getProfileImage();
            String base64Image = Base64.getEncoder().encodeToString(profileImage);
            System.out.println("프로필사진: "+ base64Image);
            return ResponseEntity.ok("data:image/jpeg;base64," + base64Image);
        } else if (user.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("프로필 사진이 없습니다.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자를 찾을 수 없습니다.");
        }
    }
}