package com.socialweb.repository;

import com.socialweb.entity.TicketCommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketCommentRepository extends JpaRepository<TicketCommentEntity, Long> {
}
