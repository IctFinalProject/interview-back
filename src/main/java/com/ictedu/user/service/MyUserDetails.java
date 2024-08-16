package com.ictedu.user.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.ictedu.user.model.entity.User;

public class MyUserDetails implements UserDetails {

	   private final User user;//엔터티다
	   
	   public MyUserDetails(User user) {
	       this.user = user;
	   }
	
	   @Override
	   public Collection<? extends GrantedAuthority> getAuthorities() {
	       List<GrantedAuthority> authorities = new ArrayList<>();
	
	       // 사용자의 isAdmin 값에 따라 ROLE_ADMIN 또는 ROLE_USER 권한을 부여합니다.
		   if (this.user.getIsAdmin()) {
		       authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
		   } else {
			   authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
	       }
	
	       return authorities;
	   }
	
	   @Override
	   public String getPassword() {
	       return user.getPassword();
	   }
	
	   @Override
	   public String getUsername() {
	       return user.getEmail();
	   }
	
	
	   @Override
	   public boolean isAccountNonExpired() {
	       return true;
	   }
	
	   @Override
	   public boolean isAccountNonLocked() {
	       return !this.user.getIsDeleted();
	       //isDeleted 가 false면 잠기지 않은 것입니다.
	   }
	
	   @Override
	   public boolean isCredentialsNonExpired() {
	       return true;
	   }
	
	   @Override
	   public boolean isEnabled() {
	       return this.user.getIsActivated();
	       //isActivated 가 true면 활성화 상태입니다.
	   }
	   
	   public String getName() {
		   return user.getUsername();
	   }
	
		public String getAddress() {
			return user.getAddress();
		}
		
		public String getPhone() {
			return user.getPhone();
		}

		public String getBirth() {
			return user.getBirth();
		}

		public String getGender() {
			return user.getGender();
		}

		public byte[] getProfile() {
			return user.getProfileImage();
		}

		public Long getId() {
			return user.getId();
		}
}
