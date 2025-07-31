package com.devit.chatapp.dto.response;

import com.devit.chatapp.enums.ConversationType;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ConversationResponseDTO {

    private Long id;
    private String name;
    private ConversationType type;
    private String avatarUrl;
    private ChatMessageResponse lastMessage;
    private Long unreadCount;
}
