package com.arinjain.chat_app.service;

import com.arinjain.chat_app.model.ChatMessage;
import com.arinjain.chat_app.repository.ChatMessageRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ChatService {

    private final ChatMessageRepository messageRepository;
    private final SimpMessagingTemplate messagingTemplate;

    // Use a thread-safe Set to store connected user names
    private final Set<String> connectedUsers = ConcurrentHashMap.newKeySet();

    public ChatService(ChatMessageRepository messageRepository, SimpMessagingTemplate messagingTemplate) {
        this.messageRepository = messageRepository;
        this.messagingTemplate = messagingTemplate;
    }

    // --- Message Handling ---

    public ChatMessage saveMessage(ChatMessage chatMessage) {
        chatMessage.setTimestamp(LocalDateTime.now()); // Set the timestamp
        return messageRepository.save(chatMessage);
    }

    public List<ChatMessage> getChatHistory() {
        List<ChatMessage> history = messageRepository.findFirst50ByOrderByTimestampDesc();
        Collections.reverse(history); // Reverse to show oldest first
        return history;
    }

    // --- User List Management ---

    public void addUser(String username) {
        connectedUsers.add(username);
        broadcastUserList();
    }

    public void removeUser(String username) {
        connectedUsers.remove(username);
        broadcastUserList();
    }

    public Set<String> getConnectedUsers() {
        System.out.println("hi ");
        return new HashSet<>(connectedUsers);
    }

    public void broadcastUserList() {
        // Send the updated user list to everyone subscribed to "/topic/users"
        messagingTemplate.convertAndSend("/topic/users", getConnectedUsers());
    }
}