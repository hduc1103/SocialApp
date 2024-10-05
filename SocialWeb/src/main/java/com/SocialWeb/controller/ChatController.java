package com.SocialWeb.controller;

import com.SocialWeb.entity.MessageEntity;
import com.SocialWeb.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Controller
public class ChatController {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private MessageService messageService;

    @MessageMapping("/private-message")
    public void recMessage(@Payload MessageEntity message) {
        // Set timestamp and save the message into MongoDB
        message.setTimestamp(LocalDateTime.now());
        messageService.saveMessage(message);

        // Send the message to the specific user
        simpMessagingTemplate.convertAndSendToUser(message.getReceiverId(), "/private", message);
    }
}
