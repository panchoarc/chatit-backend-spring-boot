package com.devit.chatapp.service;

import com.devit.chatapp.entity.ConversationMember;

import java.time.Instant;
import java.util.List;

public interface ConversationMemberService {


    List<ConversationMember> getConversationMembers(Long conversationId);
    void updatedLastMessage(Long conversationId, String keycloakId);


    ConversationMember findByConversationIdAndUserUsername(Long conversationId, String keycloakId);

    Instant getLastReadAt(Long conversationId, String keycloakId);

    void markAsRead(Long chatId, String name);
}
