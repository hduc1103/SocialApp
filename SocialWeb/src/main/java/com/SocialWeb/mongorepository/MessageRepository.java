package com.SocialWeb.mongorepository;

import com.SocialWeb.entity.MessageEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends MongoRepository<MessageEntity, String> {
    List<MessageEntity> findBySenderIdAndReceiverId(String senderId, String receiverId);
}
