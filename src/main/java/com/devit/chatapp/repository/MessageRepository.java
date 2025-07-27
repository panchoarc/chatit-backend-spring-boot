package com.devit.chatapp.repository;

import com.devit.chatapp.entity.Conversation;
import com.devit.chatapp.entity.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {


    Slice<Message> findByConversationId(Long conversationId, Pageable pageable);

    Optional<Message> findTopByConversationOrderByCreatedAtDesc(Conversation conversation);
}
