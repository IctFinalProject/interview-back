package com.ictedu.user.dto;

import java.time.LocalDateTime;

import com.ictedu.user.model.entity.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private Long id;
    private String email;
    private Boolean isEmailVerified;
    private Boolean isAdmin;
    private String gender;
    private String birth;
    private String address;
    private String username;
    private String phone;
    private byte[] profileImage;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
    private LocalDateTime deletedTime;
    private LocalDateTime lastLogin;
    private Boolean isActivated;
    private Boolean isDeleted;
    private Boolean isNaver;
    private Boolean isKakao;
    private Boolean isGoogle;
    private String snsAccessToken;

    // DTO -> Entity 변환
    public User toEntity() {
        return User.builder()
                   .id(id)
                   .email(email)
                   .isEmailVerified(isEmailVerified ? 1 : 0)
                   .isAdmin(isAdmin ? 1 : 0)
                   .gender(gender)
                   .birth(birth)
                   .address(address)
                   .username(username)
                   .phone(phone)
                   .profileImage(profileImage)
                   .createdTime(createdTime)
                   .updatedTime(updatedTime)
                   .deletedTime(deletedTime)
                   .lastLogin(lastLogin)
                   .isActivated(isActivated ? 1 : 0)
                   .isDeleted(isDeleted ? 1 : 0)
                   .isNaver(isNaver ? 1 : 0)
                   .isKakao(isKakao ? 1 : 0)
                   .isGoogle(isGoogle ? 1 : 0)
                   .snsAccessToken(snsAccessToken)
                   .build();
    }

    // Entity -> DTO 변환
    public static UserDTO toDto(User user) {
        return UserDTO.builder()
                      .id(user.getId())
                      .email(user.getEmail())
                      .isEmailVerified(user.getIsEmailVerified())
                      .isAdmin(user.getIsAdmin())
                      .gender(user.getGender())
                      .birth(user.getBirth())
                      .address(user.getAddress())
                      .username(user.getUsername())
                      .phone(user.getPhone())
                      .profileImage(user.getProfileImage())
                      .createdTime(user.getCreatedTime())
                      .updatedTime(user.getUpdatedTime())
                      .deletedTime(user.getDeletedTime())
                      .lastLogin(user.getLastLogin())
                      .isActivated(user.getIsActivated())
                      .isDeleted(user.getIsDeleted())
                      .isNaver(user.getIsNaver())
                      .isKakao(user.getIsKakao())
                      .isGoogle(user.getIsGoogle())
                      .snsAccessToken(user.getSnsAccessToken())
                      .build();
    }
}