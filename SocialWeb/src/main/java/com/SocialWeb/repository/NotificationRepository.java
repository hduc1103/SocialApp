package com.SocialWeb.repository;

import com.SocialWeb.entity.NotificationEntity;
import com.SocialWeb.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {
    List<NotificationEntity> findByUserOrderByCreatedAtDesc(UserEntity user);
}

