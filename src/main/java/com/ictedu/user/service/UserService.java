package com.ictedu.user.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.ictedu.user.model.entity.User;
import com.ictedu.user.repository.UserRepository;

@Service
public class UserService {
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	private final UserRepository userRepository;
	
	public UserService(BCryptPasswordEncoder bCryptPasswordEncoder, UserRepository userRepository) {
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
		this.userRepository = userRepository;
	}

	@Transactional
    public User signUpUser(InputUser inputUser) {
        userRepository.findByEmail(inputUser.getEmail())
                .ifPresent(u -> {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이메일이 이미 사용중입니다.");
                });
        return userRepository.save(createUser(inputUser));
    }

    private User createUser(InputUser user) {
        String encodedPassword = bCryptPasswordEncoder.encode(user.getPassword());
        return User.builder()
                .email(user.getEmail())
                .password(encodedPassword)
                .isDeleted(0) //false = 0, true = 1
                .isActivated(1) //false = 0, true = 1
                .isEmailVerified(1) //false = 0, true = 1
                .isAdmin(0) //false = 0, true = 1
                .isGoogle(0) //false = 0, true = 1
                .isKakao(0) //false = 0, true = 1
                .isNaver(0) //false = 0, true = 1
                .address(user.getAddress())
                .phone(user.getPhone())
                .gender(user.getGender())
                .birth(user.getBirth())
                .username(user.getUsername())
                .profileImage(user.getProfileImage()) // 프로필 사진 추가
                .build();
    }

    @Transactional
    public Optional<User> findByEmail(String username) {
        return userRepository.findByEmail(username);
    }
    
    @Transactional
    public User signUpSnsUser(User user) {
        userRepository.findByEmail(user.getEmail())
                .ifPresent(u -> {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이메일이 이미 사용중입니다.");
                });
        return userRepository.save(user);
    }

    @Transactional
    public Optional<User> findEmailByUsernameAndPhone(String username, String phone) {
        return userRepository.findByUsernameAndPhone(username, phone);
    }
    
    @Transactional
    public Optional<User> verifyUserByUsernameAndEmail(String username, String email) {
        return userRepository.findByUsernameAndEmail(username, email);
    }

    @Transactional
    public boolean updatePassword(String username, String email, String newPassword) {
        String encodedPassword = bCryptPasswordEncoder.encode(newPassword);
        int updatedRows = userRepository.updatePasswordByUsernameAndEmail(encodedPassword, username, email);
        return updatedRows > 0;
    }
	
}
