package com.devit.chatapp.controller;

import com.devit.chatapp.service.PresenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
@Slf4j
public class PresenceController {

    private final PresenceService presenceService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/ping")
    public void handlePing(Principal principal) {

        log.info("Received Ping request from user {}", principal.getName());
        presenceService.userHeartbeat(principal.getName());

        messagingTemplate.convertAndSend("/topic/presence",
                new UsersController.UserStatusDto(principal.getName(),true, LocalDateTime.now()));
    }
}