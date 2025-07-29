package com.devit.chatapp.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "conversation_members")
@Data
@NoArgsConstructor
public class ConversationMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Conversation conversation;

    @ManyToOne(optional = false)
    private User user;

    @Column(name = "last_read_at")
    private Instant lastReadAt;
}