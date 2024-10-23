package com.socialweb.service.impl;

import com.socialweb.entity.MessageEntity;
import com.socialweb.repository.MessageRepository;
import com.socialweb.service.interfaces.MessageService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public MessageServiceImpl(MessageRepository messageRepository, SimpMessagingTemplate simpMessagingTemplate) {
        this.messageRepository = messageRepository;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @Override
    public void sendMessage(MessageEntity message) {
        message.setTimestamp(LocalDateTime.now());
        messageRepository.save(message);
        simpMessagingTemplate.convertAndSendToUser(message.getReceiverId(), "/private", message);
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
