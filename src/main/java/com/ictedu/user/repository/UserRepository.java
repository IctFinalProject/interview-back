package com.ictedu.user.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ictedu.user.model.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{
	
	Optional<User> findByEmail(String email);
	
	Optional<User> findByEmailAndIsNaver(String email, Integer isNaver);
	
	Optional<User> findByEmailAndIsKakao(String email, Integer isKakao);
	
	Optional<User> findByEmailAndIsGoogle(String email, Integer isGoogle);

	Optional<User> findByUsernameAndPhone(String username, String phone);

	Optional<User> findByUsernameAndEmail(String username, String email);

	@Modifying
	@Query("UPDATE User u SET u.password = :password WHERE u.username = :username AND u.email = :email")
	int updatePasswordByUsernameAndEmail(@Param("password") String password, @Param("username") String username, @Param("email") String email);

}
