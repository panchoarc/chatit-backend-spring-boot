package com.devit.chatapp.dto.response;

import com.devit.chatapp.enums.MessageType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
public class ChatMessageResponse {
    private Long id;
    private String content;
    private String senderId;
    private MessageType type;
    private Instant createdAt;
}