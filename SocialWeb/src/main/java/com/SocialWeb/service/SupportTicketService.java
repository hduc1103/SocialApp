package com.SocialWeb.service;

import com.SocialWeb.entity.SupportTicketEntity;
import com.SocialWeb.entity.UserEntity;
import com.SocialWeb.repository.SupportTicketRepository;
import com.SocialWeb.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

import static com.SocialWeb.Message.*;

@Service
public class SupportTicketService {
    @Autowired
    SupportTicketRepository supportTicketRepository;
    @Autowired
    UserRepository userRepository;

    public String createTicket(String username, List<String> content ){
        SupportTicketEntity supportTicketEntity= new SupportTicketEntity();
        UserEntity userEntity = userRepository.findByUsername(username).orElseThrow();
        supportTicketEntity.setUser(userEntity);
        supportTicketEntity.setContent(content);
        supportTicketEntity.setCreatedAt(new Date());
        supportTicketRepository.save(supportTicketEntity);

        return Y_SUPPORT_TICKET;
    }

    public String updateTicket(Long userId, List<String> content, Long id){
        if(supportTicketRepository.checkUserTicket(userId)!=id){
            return DENIED_ACCESS_TICKET;
        }
        SupportTicketEntity supportTicketEntity = supportTicketRepository.findById(id).orElseThrow();
        supportTicketEntity.setContent(content);
        supportTicketRepository.save(supportTicketEntity);
        return U_SUPPORT_TICKET;
    }
}
