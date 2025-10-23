package com.arinjain.chat_app.controller;

import com.arinjain.chat_app.model.ChatMessage;
import com.arinjain.chat_app.service.ChatService;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatController(ChatService chatService, SimpMessagingTemplate messagingTemplate) {
        this.chatService = chatService;
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Handles new messages from any user.
     * Saves the message and broadcasts it to everyone on "/topic/chat".
     */
    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/chat")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        // Save the message to the database
        return chatService.saveMessage(chatMessage);
    }

    /**
     * Handles a request for chat history from a specific user.
     * This is called by the client right after connecting.
     */
    @MessageMapping("/chat.getHistory")
    public void getChatHistory(SimpMessageHeaderAccessor headerAccessor) {
        // Get the username from the WebSocket session
        String username = (String) headerAccessor.getSessionAttributes().get("username");
        if (username != null) {
            // Get history from the service
            var history = chatService.getChatHistory();

            // Send the history ONLY to the user who requested it
            messagingTemplate.convertAndSendToUser(
                    username,
                    "/queue/history", // This becomes "/user/queue/history" on the client
                    history
            );
        }
    }

    /**
     * Serves the main chat page.
     */
    @GetMapping("/chat")
    public String chat() {
        return "chat";
    }
}