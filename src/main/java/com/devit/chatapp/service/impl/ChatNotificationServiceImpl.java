package com.devit.chatapp.service.impl;

import com.devit.chatapp.dto.response.ConversationResponseDTO;
import com.devit.chatapp.service.ChatNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class ChatNotificationServiceImpl implements ChatNotificationService {

    private final SimpMessagingTemplate simpMessagingTemplate;

    @Override
    public void notifyUserOfNewChat(String keycloakId, ConversationResponseDTO conversationResponseDTO) {
        simpMessagingTemplate.convertAndSendToUser(keycloakId, "/queue/chats", conversationResponseDTO);

    }
}
