package com.arinjain.chat_app.listener;

import com.arinjain.chat_app.service.ChatService;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class WebSocketEventListener {

    private final ChatService chatService;

    public WebSocketEventListener(ChatService chatService) {
        this.chatService = chatService;
    }

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        // Get the headers from the connect event
        StompHeaderAccessor headers = StompHeaderAccessor.wrap(event.getMessage());

        // Get the "username" we (will) pass from the client
        String username = headers.getFirstNativeHeader("username");

        if (username != null) {
            // Store username in the WebSocket session attributes
            headers.getSessionAttributes().put("username", username);
            // Add user to our service and broadcast the new list
            chatService.addUser(username);
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headers = StompHeaderAccessor.wrap(event.getMessage());

        // Get the username from the session we saved earlier
        String username = (String) headers.getSessionAttributes().get("username");

        if (username != null) {
            // Remove user and broadcast the new list
            chatService.removeUser(username);
        }
    }
}