package com.devit.chatapp.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TypingNotification {

    private Long chatId;
    private String senderId;
    private String senderName;
}
