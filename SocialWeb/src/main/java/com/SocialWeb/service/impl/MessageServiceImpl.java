package com.SocialWeb.service.impl;

import com.SocialWeb.entity.MessageEntity;
import com.SocialWeb.mongorepository.MessageRepository;
import com.SocialWeb.service.interfaces.MessageService;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;

    public MessageServiceImpl(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Override
    public void saveMessage(MessageEntity message) {
        messageRepository.save(message);
    }

    @Override
    public List<MessageEntity> getMessagesBetweenUsers(String senderId, String receiverId) {
        return messageRepository.findAllMessagesBetweenUsers(senderId, receiverId);
    }

    @Override
    public List<MessageEntity> getAllMessagesForUser(String userId) {
        return messageRepository.findAllMessagesForUser(userId);
    }

    @Override
    public Set<String> getAllConversations(String userId) {
        List<MessageEntity> messages = getAllMessagesForUser(userId);
        Set<String> conversationIds = new HashSet<>();

        for (MessageEntity message : messages) {
            if (message.getSenderId().equals(userId)) {
                conversationIds.add(message.getReceiverId());
            } else {
                conversationIds.add(message.getSenderId());
            }
        }
        return conversationIds;
    }

    @Override
    public void deleteAllUserMessage(Long userId) {
        String user_id = userId.toString();
        messageRepository.deleteAllMessagesForUser(user_id);
    }
}
