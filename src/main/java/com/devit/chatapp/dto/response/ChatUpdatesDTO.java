package com.devit.chatapp.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChatUpdatesDTO {

    private Long chatId;
    private ChatMessageResponse lastMessage;
}
