package com.devit.chatapp.dto.request;

import com.devit.chatapp.enums.ConversationType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
public class CreateConversationRequest {

    private ConversationType type;     // DM o GROUP
    private String name;               // Solo requerido para grupos
    private Set<String> memberIds;       // Incluirá al creador también
}
