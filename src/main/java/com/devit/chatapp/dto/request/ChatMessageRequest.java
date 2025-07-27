package com.devit.chatapp.dto.request;

import com.devit.chatapp.enums.MessageType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
public class ChatMessageRequest {
    private String senderId;
    private String content;
    private MessageType type;
    private Instant timestamp;
}