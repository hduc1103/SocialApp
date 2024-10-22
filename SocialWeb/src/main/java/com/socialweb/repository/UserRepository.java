package com.socialweb.repository;

import com.socialweb.entity.UserEntity;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    UserEntity findByEmail(String email);

    @Query("SELECT u FROM UserEntity u WHERE (u.username LIKE %:keyword% OR u.name LIKE %:keyword%) AND u.isDeleted = false")
    List<UserEntity> searchUsersByUsername(@Param("keyword") String keyword);

    @Modifying
    @Transactional
    @Query(value = "UPDATE web_user SET is_deleted = 1 WHERE id = :userId", nativeQuery = true)
    void deleteUser(@Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE web_likes SET is_deleted = 1 WHERE user_id = :userId", nativeQuery = true)
    void deleteLikesByUserId(@Param("userId") Long userId);
}
