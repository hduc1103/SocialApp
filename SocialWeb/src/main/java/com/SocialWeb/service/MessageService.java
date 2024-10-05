package com.SocialWeb.service;

import com.SocialWeb.entity.MessageEntity;
import com.SocialWeb.mongorepository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    public List<MessageEntity> getMessagesBetweenUsers(String senderId, String receiverId) {
        return messageRepository.findBySenderIdAndReceiverId(senderId, receiverId);
    }

    public MessageEntity saveMessage(MessageEntity message) {
        return messageRepository.save(message);
    }
}
