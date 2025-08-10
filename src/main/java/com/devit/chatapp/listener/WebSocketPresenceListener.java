package com.devit.chatapp.listener;

import com.devit.chatapp.controller.UsersController;
import com.devit.chatapp.service.PresenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketPresenceListener {

    private final PresenceService presenceService;
    private final SimpMessageSendingOperations messagingTemplate;

    @EventListener
    public void handleSessionConnected(SessionConnectedEvent event) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
        if (sha.getUser() != null) {
            String username = sha.getUser().getName();
            presenceService.userConnected(username);
            messagingTemplate.convertAndSend("/topic/presence",
                    new UsersController.UserStatusDto(username, true, LocalDateTime.now()));
        } else {
            log.warn("SessionConnectedEvent without authenticated user");
        }
    }


    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
        if (sha.getUser() != null) {
            String username = sha.getUser().getName();
            presenceService.userDisconnected(username);
            messagingTemplate.convertAndSend("/topic/presence",
                    new UsersController.UserStatusDto(username, false, LocalDateTime.now()));
        }
    }
}
