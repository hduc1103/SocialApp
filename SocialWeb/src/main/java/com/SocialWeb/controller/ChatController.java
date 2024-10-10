package com.SocialWeb.controller;

import com.SocialWeb.entity.MessageEntity;
import com.SocialWeb.service.interfaces.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@RequestMapping("/chat")
@RestController
public class ChatController {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private MessageService messageService;

    @MessageMapping("/private-message")
    public void receiveMessage(@Payload MessageEntity message) {
        message.setTimestamp(LocalDateTime.now());
        messageService.saveMessage(message);
        simpMessagingTemplate.convertAndSendToUser(message.getReceiverId(), "/private", message);
    }
    @GetMapping("/conversations")
    public Set<String> getConversations(@RequestParam String userId) {
        return messageService.getAllConversations(userId);
    }
    @GetMapping("/conversation")
    public List<MessageEntity> getConversation(
            @RequestParam String senderId,
            @RequestParam String receiverId) {
        return messageService.getMessagesBetweenUsers(senderId, receiverId);
    }
}
