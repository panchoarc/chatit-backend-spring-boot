package com.devit.chatapp.repository;

import com.devit.chatapp.entity.ConversationMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConversationMemberRepository extends JpaRepository<ConversationMember, Long> {
    List<ConversationMember> findByConversationId(Long id);
}
