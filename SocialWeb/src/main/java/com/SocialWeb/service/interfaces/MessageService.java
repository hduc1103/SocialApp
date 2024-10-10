package com.SocialWeb.service.interfaces;

import com.SocialWeb.entity.MessageEntity;
import java.util.List;
import java.util.Set;

public interface MessageService {

    void saveMessage(MessageEntity message);

    List<MessageEntity> getMessagesBetweenUsers(String senderId, String receiverId);

    List<MessageEntity> getAllMessagesForUser(String userId);

    Set<String> getAllConversations(String userId);

    void deleteAllUserMessage(Long userId);
}
