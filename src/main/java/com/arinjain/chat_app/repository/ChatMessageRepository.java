package com.arinjain.chat_app.repository;

import com.arinjain.chat_app.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    // This is a custom query Spring Data writes for you
    // It finds the most recent 50 messages
    List<ChatMessage> findFirst50ByOrderByTimestampDesc();
}