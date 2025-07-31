package com.devit.chatapp.service.impl;

import com.devit.chatapp.entity.ConversationMember;
import com.devit.chatapp.repository.ConversationMemberRepository;
import com.devit.chatapp.service.ConversationMemberService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConversationMemberServiceImpl implements ConversationMemberService {

    private final ConversationMemberRepository conversationMemberRepository;

    @Override
    public List<ConversationMember> getConversationMembers(Long conversationId) {
        return conversationMemberRepository.findByConversationId(conversationId);
    }


    @Override
    @Transactional
    public void updatedLastMessage(Long conversationId, String keycloakId) {
        Instant now = Instant.now();
        ConversationMember member = findByConversationIdAndUserUsername(conversationId, keycloakId);
        member.setLastReadAt(now);
        conversationMemberRepository.save(member);

    }

    @Override
    public ConversationMember findByConversationIdAndUserUsername(Long conversationId, String keycloakId) {
        return conversationMemberRepository.findByConversationIdAndUser_KeycloakId(conversationId, keycloakId);
    }

    @Override
    public Instant getLastReadAt(Long conversationId, String keycloakId) {
        ConversationMember member = findByConversationIdAndUserUsername(conversationId, keycloakId);
        return member.getLastReadAt();
    }


    @Override
    @Transactional
    public void markAsRead(Long conversationId, String keycloakId) {
        ConversationMember member = conversationMemberRepository.findByConversationIdAndUser_KeycloakId(conversationId, keycloakId);
        member.setLastReadAt(Instant.now());
        conversationMemberRepository.save(member);
    }
}
