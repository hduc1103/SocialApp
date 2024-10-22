package com.SocialWeb.repository;

import com.SocialWeb.entity.NotificationEntity;
import com.SocialWeb.entity.UserEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {
    List<NotificationEntity> findByUserOrderByCreatedAtDesc(UserEntity user);

    @Query(value = "SELECT * FROM notification WHERE related_id= :relatedId AND sender_id= :senderId", nativeQuery = true)
    Optional<NotificationEntity> findNotificationByRelatedIdAndSenderId(@Param("relatedId") Long relatedId, @Param("senderId") Long senderId);
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM notification WHERE id= :id", nativeQuery = true)
    void deleteNotificationById(@Param("id") Long id);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM notification WHERE user_id = :userId OR sender_id= :userId", nativeQuery = true)
    void deleteAllUserNotification(@Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM notification WHERE related_id= :post_or_cmt_id", nativeQuery = true)
    void deletePostOrCommentNoti(@Param("post_or_cmt_id") Long post_or_cmt_id);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM notification WHERE user_id = :userId AND sender_id = :senderId AND type = 'friendship'", nativeQuery = true)
    void deleteFriendshipNotification(@Param("userId") Long userId, @Param("senderId") Long senderId);
}

