package com.SocialWeb.controller;

import com.SocialWeb.entity.MessageEntity;
import com.SocialWeb.service.MessageService;
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
    public void recMessage(@Payload MessageEntity message) {
        message.setTimestamp(LocalDateTime.now());
        messageService.saveMessage(message);

        simpMessagingTemplate.convertAndSendToUser(message.getReceiverId(), "/private", message);
    }
    @GetMapping("/conversations")
    public Set<String> getConversations(@RequestParam String userId) {
        System.out.println("flag1");
        return messageService.getAllConversations(userId);
    }
    @GetMapping("/conversation")
    public List<MessageEntity> getConversation(
            @RequestParam String senderId,
            @RequestParam String receiverId) {
        System.out.println("Fetching conversation between senderId: " + senderId + " and receiverId: " + receiverId);
        List<MessageEntity> messages = messageService.getMessagesBetweenUsers(senderId, receiverId);
        System.out.println("Messages fetched: " + messages);
        return messages;
    }

}
