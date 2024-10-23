package com.socialweb.repository;

import com.socialweb.entity.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<MessageEntity, Long> {

    @Query("SELECT m FROM MessageEntity m WHERE m.senderId = ?1 OR m.receiverId = ?1")
    List<MessageEntity> findAllMessagesForUser(String userId);

    @Query("SELECT m FROM MessageEntity m WHERE (m.senderId = ?1 AND m.receiverId = ?2) OR (m.senderId = ?2 AND m.receiverId = ?1)")
    List<MessageEntity> findAllMessagesBetweenUsers(String senderId, String receiverId);

    @Transactional
    @Modifying
    @Query("DELETE FROM MessageEntity m WHERE m.senderId = ?1 OR m.receiverId = ?1")
    void deleteAllMessagesForUser(String userId);
}
