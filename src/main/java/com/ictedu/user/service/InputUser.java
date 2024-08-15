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

}
