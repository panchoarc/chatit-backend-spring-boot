package com.devit.chatapp.repository;

import com.devit.chatapp.dto.ConversationWithLastMessageDTO;
import com.devit.chatapp.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    boolean existsByIdAndMembers_Id(Long conversationId, Long userId);


    @Query("""
                SELECT u.user.keycloakId
                FROM Conversation c
                JOIN c.members u
                WHERE c.id = :chatId
            """)
    List<String> findMemberKeycloakIdsByChatId(@Param("chatId") Long chatId);


    @Query(value = """
            SELECT DISTINCT 
                c.id AS conversationId,
                c.type AS type,
                CASE 
                    WHEN c.type = 'GROUP' THEN c.name
                    ELSE CONCAT(other_user.first_name, ' ', other_user.last_name)
                END AS name,
                CASE 
                    WHEN c.type = 'GROUP' THEN NULL
                    ELSE other_user.avatar_url
                END AS avatar,
                m.id AS lastMessageId,
                m.type AS lastMessageType,
                m.content AS lastMessageContent,
                m.created_at AS lastMessageCreatedAt,
                sender.keycloak_id AS lastMessageSenderId
            FROM conversations c
            JOIN conversation_members cm_self ON cm_self.conversation_id = c.id
            JOIN users self_user ON self_user.id = cm_self.user_id
            -- El usuario actual
            LEFT JOIN conversation_members cm_other ON cm_other.conversation_id = c.id AND cm_other.user_id != self_user.id
            LEFT JOIN users other_user ON other_user.id = cm_other.user_id
            -- Último mensaje
            LEFT JOIN LATERAL (
                SELECT m.id, m.content, m.created_at, m.sender_id, m.type
                FROM messages m
                WHERE m.conversation_id = c.id
                ORDER BY m.created_at DESC
                LIMIT 1
            ) m ON true
            LEFT JOIN users sender ON sender.id = m.sender_id
            WHERE self_user.keycloak_id = :keycloakId
            ORDER BY m.created_at DESC NULLS LAST
            """, nativeQuery = true)
    List<ConversationWithLastMessageDTO> findConversationsWithLastMessageByKeycloakId(@Param("keycloakId") String keycloakId);

}
