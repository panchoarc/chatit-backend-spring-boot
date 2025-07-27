package com.devit.chatapp.dto;

import java.time.Instant;

public interface ConversationWithLastMessageDTO {
    Long getConversationId();

    String getName();

    String getType();

    String getAvatar();

    Long getLastMessageId();

    String getLastMessageContent();

    Instant getLastMessageCreatedAt();

    String getLastMessageSenderId();
    String getLastMessageType();
}
