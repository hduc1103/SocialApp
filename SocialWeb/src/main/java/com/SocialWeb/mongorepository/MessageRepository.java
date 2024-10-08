package com.SocialWeb.mongorepository;

import com.SocialWeb.entity.MessageEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface MessageRepository extends MongoRepository<MessageEntity, String> {
    @Query("{'$or': [{'senderId': ?0}, {'receiverId': ?0}]}")
    List<MessageEntity> findAllMessagesForUser(String userId);
    @Query("{ '$or': [ { '$and': [ { 'senderId': ?0 }, { 'receiverId': ?1 } ] }, { '$and': [ { 'senderId': ?1 }, { 'receiverId': ?0 } ] } ] }")
    List<MessageEntity> findAllMessagesBetweenUsers(String senderId, String receiverId);

    @Transactional
    @Modifying
    @Query(value = "{ '$or': [{'senderId': ?0}, {'receiverId': ?0}] }", delete = true)
    void deleteAllMessagesForUser(String userId);
}
