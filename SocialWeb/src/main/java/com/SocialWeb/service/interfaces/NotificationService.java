package com.SocialWeb.service.interfaces;

import com.SocialWeb.domain.response.NotificationResponse;
import com.SocialWeb.entity.UserEntity;

import java.util.List;

public interface NotificationService {
    <T> void sendNotification(UserEntity receiver, String content, T entity, Long senderId);

    <T> void saveNotification(UserEntity receiver, String content, T entity, Long senderId);

    List<NotificationResponse> getAllNotifications(String token);

    <T> void removeNotification(Long userId, Long relatedId);
}
