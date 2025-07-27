package com.devit.chatapp.service;

import com.devit.chatapp.dto.request.AddMemberRequest;
import com.devit.chatapp.dto.request.CreateConversationRequest;
import com.devit.chatapp.dto.response.ConversationResponseDTO;
import com.devit.chatapp.dto.response.UserResponseDTO;
import com.devit.chatapp.entity.Conversation;

import java.util.List;

public interface ConversationService {

    Conversation findById(Long conversationId);

    boolean isMember(Long conversationId, Long userId);

    void createConversation(CreateConversationRequest conversation, String creatorId);

    List<ConversationResponseDTO> findMyConversations(String keycloakId);

    List<UserResponseDTO> getMembersByChatId(Long chatId);

    void addMemberToConversation(Long conversationId, AddMemberRequest memberRequest);

    List<String> findMembersByChatId(Long chatId);

}
