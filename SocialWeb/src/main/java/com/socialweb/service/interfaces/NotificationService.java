package com.socialweb.service.interfaces;

import com.socialweb.domain.response.NotificationResponse;
import com.socialweb.entity.UserEntity;

import java.util.List;

public interface NotificationService {
    void sendGlobalNotification(String content);

    void sendUserNotification(Long userId, String content);

    <T> void sendNotification(UserEntity receiver, String content, T entity, Long senderId);

    List<NotificationResponse> getAllNotifications(String token);

    <T> void removeNotification(Long userId, Long relatedId);

    void deleteAllUserNotification(Long userId);

    void deleteAllPostOrCommentNoti(Long id);

    void deleteFriendShipNoti(Long userId, Long senderId);
}
