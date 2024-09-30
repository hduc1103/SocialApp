package com.SocialWeb.repository;

import com.SocialWeb.entity.TicketCommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketCommentRepository extends JpaRepository<TicketCommentEntity, Long> {
}
