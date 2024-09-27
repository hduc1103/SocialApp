package com.SocialWeb.repository;

import com.SocialWeb.entity.SupportTicketEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SupportTicketRepository extends JpaRepository<SupportTicketEntity, Long> {
    @Query(value = "SELECT id FROM ticket_support WHERE user_id = :userId", nativeQuery = true)
    int checkUserTicket(@Param("userId") Long userId);
}
