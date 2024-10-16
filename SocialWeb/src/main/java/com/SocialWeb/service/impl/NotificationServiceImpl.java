package com.SocialWeb.service.impl;

import com.SocialWeb.domain.response.NotificationResponse;
import com.SocialWeb.entity.NotificationEntity;
import com.SocialWeb.entity.UserEntity;
import com.SocialWeb.repository.NotificationRepository;
import com.SocialWeb.repository.UserRepository;
import com.SocialWeb.security.JwtUtil;
import com.SocialWeb.service.interfaces.NotificationService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository, SimpMessagingTemplate simpMessagingTemplate, JwtUtil jwtUtil, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    private String extractUsername(String token) {
        String jwtToken = token.substring(7);
        return jwtUtil.extractUsername(jwtToken);
    }

    @Override
    public void sendNotification(UserEntity user, String content) {
        System.out.println("Sending notification to userID: " + user.getId());
        simpMessagingTemplate.convertAndSendToUser(
                String.valueOf(user.getId()),
                "/queue/notifications",
                content
        );

        System.out.println("Notification sent to user: " + user.getId() + " with content: " + content);

        saveNotification(user, content);
    }


    @Override
    public NotificationEntity saveNotification(UserEntity user, String content) {
        NotificationEntity notification = NotificationEntity.builder()
                .user(user)
                .content(content)
                .createdAt(new Date())
                .build();
        return notificationRepository.save(notification);
    }

    @Override
    public List<NotificationResponse> getAllNotifications(String token) {
        String username = extractUsername(token);

        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        List<NotificationEntity> notifications = notificationRepository.findByUserOrderByCreatedAtDesc(userEntity);

        return notifications.stream()
                .map(notification -> new NotificationResponse(
                        notification.getId(),
                        notification.getUser().getId(),
                        notification.getCreatedAt(),
                        notification.getContent()
                ))
                .collect(Collectors.toList());
    }

}
