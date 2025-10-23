package com.arinjain.chat_app.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime; // Use modern time API

@Entity // <-- Make it a database entity
@Data
@NoArgsConstructor
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Use Long for database IDs
    private String sender;
    private String content;
    private LocalDateTime timestamp; // <-- Add a timestamp
}