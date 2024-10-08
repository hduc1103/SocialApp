package com.SocialWeb.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Document(collection = "messages") 
public class MessageEntity {
    @Id
    private String id;  

    private String senderId;      
    private String receiverId;    
    private String content;        
    private LocalDateTime timestamp;  
    private String status;
}
