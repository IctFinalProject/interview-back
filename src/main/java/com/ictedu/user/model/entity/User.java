package com.ictedu.user.model.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;


import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users") //테이블 이름
public class User {
	
	@Id
	@SequenceGenerator(name="seq_id",sequenceName = "seq_id",allocationSize = 1,initialValue = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "seq_id")
	private Long id;
	
    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "email_verified", nullable = false)
    private Integer isEmailVerified;

    @Column(name = "is_admin", nullable = false)
    private Integer isAdmin = 0;

    @Column(name = "gender", nullable = true, length = 10)
    private String gender;

    @Column(name = "birth")
    private String birth;

    @Column(name = "address", nullable = true)
    private String address;
    
    @Column(name = "username", nullable = true)
    private String username;

    @Column(name = "phone", nullable = true)
    private String phone;

    @Lob
    @Column(name = "profile_image")
    private byte[] profileImage;

    @ColumnDefault("SYSDATE")
    @CreationTimestamp
    @Column(name = "created_time")
    private LocalDateTime createdTime;

    @Column(name = "updated_time")
    private LocalDateTime updatedTime;

    @Column(name = "deleted_time")
    private LocalDateTime deletedTime;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @Column(name = "is_activated")
    private Integer isActivated = 1;
    
    @Column(name = "is_deleted")
    private Integer isDeleted = 0;

	@Column(name = "is_naver")
    private Integer isNaver = 0;

    @Column(name = "is_kakao")
    private Integer isKakao = 0;

    @Column(name = "is_google")
    private Integer isGoogle = 0;
    
    @Column(name = "sns_access_token", nullable = true)
    private String snsAccessToken;

    // Getter와 Setter 메소드
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean getIsEmailVerified() {
        return isEmailVerified == 1 ? true : false;
    }

    public void setIsEmailVerified(Integer isEmailVerified) {
        this.isEmailVerified = isEmailVerified;
    }

    public boolean getIsAdmin() {
        return isAdmin == 1 ? true : false;
    }

    public void setIsAdmin(Integer isAdmin) {
        this.isAdmin = isAdmin;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public byte[] getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(byte[] profileImage) {
        this.profileImage = profileImage;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public LocalDateTime getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(LocalDateTime updatedTime) {
        this.updatedTime = updatedTime;
    }

    public LocalDateTime getDeletedTime() {
        return deletedTime;
    }

    public void setDeletedTime(LocalDateTime deletedTime) {
        this.deletedTime = deletedTime;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public boolean getIsActivated() {
        return isActivated == 1 ? true : false;
    }

    public void setIsActivated(Integer isActivated) {
        this.isActivated = isActivated;
    }

    public boolean getIsNaver() {
        return isNaver == 1 ? true : false;
    }

    public void setIsNaver(Integer isNaver) {
        this.isNaver = isNaver;
    }

    public boolean getIsKakao() {
        return isKakao == 1 ? true : false;
    }

    public void setIsKakao(Integer isKakao) {
        this.isKakao = isKakao;
    }

    public boolean getIsGoogle() {
        return isGoogle == 1 ? true : false;
    }

    public void setIsGoogle(Integer isGoogle) {
        this.isGoogle = isGoogle;
    }

    public boolean getIsDeleted() {
    	return isDeleted == 1 ? true : false;
	}

	public void setIsDeleted(Integer isDeleted) {
		this.isDeleted = isDeleted;
	}
	
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
   

}
