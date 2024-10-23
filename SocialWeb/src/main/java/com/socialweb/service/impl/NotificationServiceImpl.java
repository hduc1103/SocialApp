package com.socialweb.service.impl;

import com.socialweb.domain.response.NotificationResponse;
import com.socialweb.entity.NotificationEntity;
import com.socialweb.entity.UserEntity;
import com.socialweb.repository.NotificationRepository;
import com.socialweb.repository.UserRepository;
import com.socialweb.security.JwtUtil;
import com.socialweb.service.interfaces.NotificationService;
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

    private final String userSubcribeRoute = "/queue/notifications";
    @Override
    public void sendGlobalNotification(String content) {
        String finalContent = "Admin: " + content;

        List<UserEntity> allUsers = userRepository.findAll();
        List<NotificationEntity> notifications = allUsers.stream()
                .map(user -> NotificationEntity.builder()
                        .user(user)
                        .content(finalContent)
                        .createdAt(new Date())
                        .relatedId(null)
                        .type("admin")
                        .senderId(3L)
                        .build())
                .toList();

        notificationRepository.saveAll(notifications);

        allUsers.forEach(user -> simpMessagingTemplate.convertAndSendToUser(
                user.getId().toString(),
                userSubcribeRoute,
                finalContent
        ));
    }

    @Override
    public void sendUserNotification(Long userId, String content) {
        content = "Admin: " + content;
        simpMessagingTemplate.convertAndSendToUser(
                String.valueOf(userId),
                userSubcribeRoute,
                content
        );
        saveAdminNotification(userId, content);
    }
    private String extractUsername(String token) {
        String jwtToken = token.substring(7);
        return jwtUtil.extractUsername(jwtToken);
    }

    private <T> String getEntityType(T entity) {
        try {
            return entity.getClass().getSimpleName();
        } catch (Exception e) {
            return "Unknown Entity";
        }
    }

    private  <T> Long getEntityId(T entity) {
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
                userSubcribeRoute,
                content
        );
        saveNotification(receiver, content, entity, senderId);
    }

    private <T> void saveNotification(UserEntity receiver, String content, T entity, Long senderId) {
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

    private void saveAdminNotification(Long userId, String content){
        NotificationEntity notification = NotificationEntity.builder()
                .user(userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("User not found")))
                .content(content)
                .createdAt(new Date())
                .relatedId(null)
                .type("admin")
                .senderId(3L)
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
                .map(notification -> {
                    UserEntity senderEntity = userRepository.findById(notification.getSenderId())
                            .orElseThrow(() -> new NoSuchElementException("Sender not found"));

                    return NotificationResponse.builder()
                            .id(notification.getId())
                            .relatedId(notification.getRelatedId())
                            .type(notification.getType())
                            .userId(notification.getUser().getId())
                            .createdAt(notification.getCreatedAt())
                            .content(notification.getContent())
                            .senderId(notification.getSenderId())
                            .imgUrl(senderEntity.getImg_url())
                            .build();
                })
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
