package com.ictedu.user.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
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
    
    @PostMapping("/edituser")
    public ResponseEntity<?> editUser(
            @RequestParam("email") String email,
            @RequestParam("username") String username,
            @RequestParam("address") String address,
            @RequestParam("birth") String birth,
            @RequestParam(value = "profileImage", required = false) MultipartFile profileImage) {
        try {
			InputUser inputUser = new InputUser(
			        email, 
			        username, 
			        address, 
			        birth, 
			        profileImage != null ? profileImage.getBytes() : null);
	        boolean isUpdated = userService.updateUser(inputUser);
	        System.out.println("isUpdated: " + isUpdated);
	        
	        if (isUpdated) {
	            return ResponseEntity.ok("유저정보가 성공적으로 변경되었습니다.");
	        } else {
	            return ResponseEntity.status(515).body("유저정보 변경에 실패하였습니다.");
	        }
		} catch (IOException e) {
			return ResponseEntity.status(514).body("프로필 사진에서 오류가 발생하였습니다.");
		}
    }
    
	//    @GetMapping("/findAllUser")
	//    public List<?> getAllUsers() {
	//        List<User> user = userService.getAllUsers();
	//        System.out.println("user: "+user);
	//		return user;
	//    }
    @GetMapping("/findAllUser")
    public List<Map<String, Object>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        
        // 결과를 저장할 리스트
        List<Map<String, Object>> result = new ArrayList<>();

        for (User user : users) {
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("id", user.getId());
            userMap.put("email", user.getEmail());
            userMap.put("username", user.getUsername());
            userMap.put("address", user.getAddress());
            userMap.put("birth", user.getBirth());
            userMap.put("createdTime", user.getCreatedTime());
            userMap.put("deletedTime", user.getDeletedTime());
            userMap.put("gender", user.getGender());
            userMap.put("isActivated", user.getIsActivated());
            userMap.put("isAdmin", user.getIsAdmin());
            userMap.put("isDeleted", user.getIsDeleted());
            userMap.put("isEmailVerified", user.getIsEmailVerified());
            userMap.put("isGoogle", user.getIsGoogle());
            userMap.put("isKakao", user.getIsKakao());
            userMap.put("isNaver", user.getIsNaver());
            userMap.put("lastLogin", user.getLastLogin());
            userMap.put("password", user.getPassword());
            userMap.put("phone", user.getPhone());
            userMap.put("snsAccessToken", user.getSnsAccessToken());
            userMap.put("updatedTime", user.getUpdatedTime());

            // 프로필 이미지를 "data:image/jpeg;base64," 형식으로 변환
            if (user.getProfileImage() != null) {
                byte[] base64Image = user.getProfileImage(); // 이미 Base64로 인코딩된 값이라고 가정
                userMap.put("profileImage", "data:image/jpeg;base64," + base64Image);
            } else {
                userMap.put("profileImage", null);
            }

            result.add(userMap);
        }
        System.out.println("결과 출력!");
        return result;
    }
    
    @GetMapping("/findUserByEmail")
    public ResponseEntity<?> findUserByEmail(@RequestParam("email") String email){
    	Optional<User> user = userService.findByEmail(email);
    	System.out.println("이메일로 찾은 유저: "+ user);
    	return ResponseEntity.ok(user.get());
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