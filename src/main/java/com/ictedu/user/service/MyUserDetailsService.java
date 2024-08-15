package com.ictedu.user.service;

import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.ictedu.user.model.entity.User;
import com.ictedu.user.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MyUserDetailsService implements UserDetailsService{
	
	private final UserRepository userRepository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	
	public MyUserDetailsService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
		this.userRepository = userRepository;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
	}
	
	@Override
	public UserDetails loadUserByUsername(String username) {
		User userData = validateUser(new InputUser(username));
		return new MyUserDetails(userData);
	}

	private User validateUser(InputUser inputUser) {
		User user = userRepository.findByEmail(inputUser.getEmail())
				.orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 이메일입니다: "+inputUser.getEmail()));
		
		if (user.getIsDeleted()) {
			throw new DisabledException("삭제된 계정입니다: "+inputUser.getEmail());
		}
		
		if (!user.getIsActivated()) {
			throw new LockedException("비활성화된 계정입니다: "+inputUser.getEmail());
		}
		return user;
	}
	
	
	
}
