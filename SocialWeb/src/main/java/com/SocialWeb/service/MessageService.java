package com.SocialWeb.service;

import com.SocialWeb.entity.MessageEntity;
import com.SocialWeb.mongorepository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    public MessageEntity saveMessage(MessageEntity message) {
        return messageRepository.save(message);
    }

    public List<MessageEntity> getMessagesBetweenUsers(String senderId, String receiverId) {
        return messageRepository.findAllMessagesBetweenUsers(senderId, receiverId);
    }

    public List<MessageEntity> getAllMessagesForUser(String userId) {
        return messageRepository.findAllMessagesForUser(userId);
    }
    public Set<String> getAllConversations(String userId) {
        List<MessageEntity> messages = getAllMessagesForUser(userId);
        return messages.stream()
                .flatMap(msg -> msg.getSenderId().equals(userId) ?
                        List.of(msg.getReceiverId()).stream() : List.of(msg.getSenderId()).stream())
                .collect(Collectors.toSet());
    }
}