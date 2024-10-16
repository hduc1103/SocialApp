package com.SocialWeb.repository;

import com.SocialWeb.entity.WebFriendEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;

import java.util.List;
import java.util.Optional;

public interface WebFriendRepository extends JpaRepository<WebFriendEntity, Long> {
    @Query(value = """
        SELECT COUNT(*) > 0 FROM web_friends 
        WHERE (user_id1 = :userId1 AND user_id2 = :userId2) 
        OR (user_id1 = :userId2 AND user_id2 = :userId1)
    """, nativeQuery = true)
    boolean existsByUserId1AndUserId2(Long userId1, Long userId2);

    @Query(value = """
        SELECT * FROM web_friends 
        WHERE (user_id1 = :userId1 AND user_id2 = :userId2) 
        OR (user_id1 = :userId2 AND user_id2 = :userId1)
    """, nativeQuery = true)
    Optional<WebFriendEntity> findByUserId1AndUserId2(Long userId1, Long userId2);

    @Modifying
    @Transactional
    @Query(value = """
        DELETE FROM web_friends 
        WHERE (user_id1 = :userId1 AND user_id2 = :userId2) 
        OR (user_id1 = :userId2 AND user_id2 = :userId1)
    """, nativeQuery = true)
    void unfriend(@Param("userId1") Long userId1, @Param("userId2") Long userId2);

    @Modifying
    @Transactional
    @Query(value = """
        UPDATE web_friends 
        SET user1_accepted = CASE 
            WHEN user_id1 = :userId THEN 1 ELSE user1_accepted END,
            user2_accepted = CASE 
            WHEN user_id2 = :userId THEN 1 ELSE user2_accepted END
        WHERE (user_id1 = :userId1 AND user_id2 = :userId2)
        OR (user_id1 = :userId2 AND user_id2 = :userId1)
    """, nativeQuery = true)
    void acceptFriendRequest(@Param("userId") Long userId, @Param("userId1") Long userId1, @Param("userId2") Long userId2);

    @Query(value = """
        SELECT user_id2 FROM web_friends 
        WHERE user_id1 = :userId AND user1_accepted = 1 AND user2_accepted = 1
        UNION 
        SELECT user_id1 FROM web_friends 
        WHERE user_id2 = :userId AND user1_accepted = 1 AND user2_accepted = 1
    """, nativeQuery = true)
    List<Long> findFriendsByUserId(@Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM web_friends WHERE user_id1 = :userId OR user_id2 = :userId", nativeQuery = true)
    void deleteAllUserRelationships(@Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query(value ="DELETE FROM web_friends WHERE user_id1= :userId1 AND user_id2= :userId2", nativeQuery = true)
    void cancelFriendRequest(@Param("userId1") Long userId1, @Param("userId2") Long userId2);
}
