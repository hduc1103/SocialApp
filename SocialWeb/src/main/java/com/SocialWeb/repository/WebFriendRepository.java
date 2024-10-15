package com.SocialWeb.repository;

import com.SocialWeb.entity.WebFriendEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WebFriendRepository extends JpaRepository<WebFriendEntity, Long> {
    boolean existsByUserId1AndUserId2(Long userId1, Long userId2);
    Optional<WebFriendEntity> findByUserId1AndUserId2(Long userId1, Long userId2);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM web_friends WHERE (user_id1 = :userId1 AND user_id2 = :userId2) OR (user_id1 = :userId2 AND user_id2 = :userId1)", nativeQuery = true)
    void unfriend(@Param("userId1") Long userId1, @Param("userId2") Long userId2);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM web_friends WHERE user_id1 = :userId OR user_id2= :userId", nativeQuery = true)
    void deleteRelationship(@Param("userId") long userId);

    @Query(value = "SELECT user_id2 FROM web_friends WHERE user_id1 = :userId UNION SELECT user_id1 FROM web_friends WHERE user_id2 = :userId", nativeQuery = true)
    List<Long> findFriendsByUserId(@Param("userId") Long userId);
}
