package com.SocialWeb.repository;

import com.SocialWeb.entity.UserEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    UserEntity findByEmail(String email);

    @Query("SELECT u FROM UserEntity u WHERE u.username LIKE %:keyword% OR u.name LIKE %:keyword%")
    List<UserEntity> searchUsersByUsername(@Param("keyword") String keyword);

}
