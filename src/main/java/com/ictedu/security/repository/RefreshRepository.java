package com.ictedu.security.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ictedu.security.model.entity.RefreshToken;


@Repository
public interface RefreshRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByTokenValue(String tokenValue);

    Boolean existsByTokenValue(String refresh);

    void deleteByTokenValue(String refresh);

    Optional<RefreshToken> findByUserId(Long long1);

}
