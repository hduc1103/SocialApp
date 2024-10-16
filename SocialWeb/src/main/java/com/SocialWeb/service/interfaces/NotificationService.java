package com.SocialWeb.service.interfaces;

import com.SocialWeb.domain.response.NotificationResponse;
import com.SocialWeb.entity.NotificationEntity;
import com.SocialWeb.entity.UserEntity;

import java.util.List;

public interface NotificationService {
    void sendNotification(UserEntity user, String content);
    NotificationEntity saveNotification(UserEntity user, String content);

    List<NotificationResponse> getAllNotifications(String token);
}
