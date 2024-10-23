package com.socialweb.repository;

import com.socialweb.entity.WebFriendEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WebFriendRepository extends JpaRepository<WebFriendEntity, Long> {
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
