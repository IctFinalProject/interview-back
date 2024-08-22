package com.ictedu.user.service;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InputUser {
    private String email;
    private String password;
    private String username;
    private String phone;
    private String address;
    private String gender;
    private String birth;
    private byte[] profileImage;

    public InputUser(String email) {
        this.email = email;
    }

	public InputUser(String email2, String username2, String address2, String birth2, byte[] profileImage2) {
		this.email = email2;
		this.username = username2;
		this.address = address2;
		this.birth = birth2;
		this.profileImage = profileImage2;
	}


}
