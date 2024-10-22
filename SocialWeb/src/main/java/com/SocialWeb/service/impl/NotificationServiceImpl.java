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

import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
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

    public <T> String getEntityType(T entity) {
        try {
            return entity.getClass().getSimpleName();
        } catch (Exception e) {
            return "Unknown Entity";
        }
    }

    public <T> Long getEntityId(T entity) {
        try {
            Field field = entity.getClass().getDeclaredField("id");
            field.setAccessible(true);
            return (Long) field.get(entity);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public <T> void sendNotification(UserEntity receiver, String content, T entity, Long senderId) {
        simpMessagingTemplate.convertAndSendToUser(
                String.valueOf(receiver.getId()),
                "/queue/notifications",
                content
        );
        saveNotification(receiver, content, entity, senderId);
    }

    public <T> void saveNotification(UserEntity receiver, String content, T entity, Long senderId) {
        Long relatedId = getEntityId(entity);
        String classType = getEntityType(entity);
        String type = "";
        if (Objects.equals(classType, "PostEntity")) {
            type = "post";
        } else if (Objects.equals(classType, "CommentEntity")) {
            type = "comment";
        } else if (Objects.equals(classType, "WebFriendEntity")) {
            type = "friendship";
        }
        NotificationEntity notification = NotificationEntity.builder()
                .user(receiver)
                .content(content)
                .createdAt(new Date())
                .relatedId(relatedId)
                .type(type)
                .senderId(senderId)
                .build();
        notificationRepository.save(notification);
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

    public <T> void removeNotification(Long relatedId, Long userId) {
        NotificationEntity notification = notificationRepository
                .findNotificationByRelatedIdAndSenderId(relatedId, userId)
                .orElseThrow(() -> new IllegalArgumentException("No notification found for relatedId " + relatedId + " and senderId " + userId));

        notificationRepository.deleteNotificationById(notification.getId());
    }

    @Override
    public void deleteAllUserNotification(Long userId) {
        notificationRepository.deleteAllUserNotification(userId);
    }

    @Override
    public void deleteAllPostOrCommentNoti(Long id) {
        notificationRepository.deletePostOrCommentNoti(id);
    }

    @Override
    public void deleteFriendShipNoti(Long userId, Long senderId) {
        notificationRepository.deleteFriendshipNotification(userId, senderId);
    }
}
