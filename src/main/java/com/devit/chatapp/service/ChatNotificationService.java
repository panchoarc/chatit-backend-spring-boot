package com.devit.chatapp.service;

import com.devit.chatapp.dto.response.ConversationResponseDTO;

public interface ChatNotificationService {


    void notifyUserOfNewChat(String keycloakId, ConversationResponseDTO chatMessageDTO);


}
