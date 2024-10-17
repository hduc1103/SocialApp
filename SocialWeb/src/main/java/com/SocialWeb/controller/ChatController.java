package com.SocialWeb.controller;

import com.SocialWeb.entity.MessageEntity;
import com.SocialWeb.service.interfaces.MessageService;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RequestMapping("/chat")
@RestController
public class ChatController {

    private final MessageService messageService;

    public ChatController( MessageService messageService) {
        this.messageService = messageService;
    }

    @MessageMapping("/private-message")
    public void receiveMessage(@Payload MessageEntity message) {
        messageService.sendMessage(message);
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
