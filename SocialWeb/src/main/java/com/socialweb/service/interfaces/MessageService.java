package com.socialweb.service.interfaces;

import com.socialweb.entity.MessageEntity;

import java.util.List;
import java.util.Set;

public interface MessageService {

    void sendMessage(MessageEntity message);

    List<MessageEntity> getMessagesBetweenUsers(String senderId, String receiverId);

    List<MessageEntity> getAllMessagesForUser(String userId);

    Set<String> getAllConversations(String userId);

    void deleteAllUserMessage(Long userId);
}
