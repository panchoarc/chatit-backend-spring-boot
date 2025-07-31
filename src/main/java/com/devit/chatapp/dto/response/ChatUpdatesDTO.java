package com.devit.chatapp.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
public class ChatUpdatesDTO {

    private Long chatId;
    private ChatMessageResponse lastMessage;
    private Instant lastMessageTimestamp;
    private Long unreadCount;
}
