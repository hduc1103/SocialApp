package com.SocialWeb.service;

import com.SocialWeb.entity.MessageEntity;
import com.SocialWeb.mongorepository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    public void saveMessage(MessageEntity message) {
        messageRepository.save(message);
    }

    public List<MessageEntity> getMessagesBetweenUsers(String senderId, String receiverId) {
        return messageRepository.findAllMessagesBetweenUsers(senderId, receiverId);
    }

    public List<MessageEntity> getAllMessagesForUser(String userId) {
        return messageRepository.findAllMessagesForUser(userId);
    }
    public Set<String> getAllConversations(String userId) {
        List<MessageEntity> messages = getAllMessagesForUser(userId);
        Set<String> conversationIds = new HashSet<>();

        for (MessageEntity message : messages) {
            if (message.getSenderId().equals(userId)) {
                conversationIds.add(message.getReceiverId());
            }
            else {
                conversationIds.add(message.getSenderId());
            }
        }
        return conversationIds;
    }

    public void deleteAllUserMessage(Long userId){
        String user_id = userId.toString();
        messageRepository.deleteAllMessagesForUser(user_id);
    }
}