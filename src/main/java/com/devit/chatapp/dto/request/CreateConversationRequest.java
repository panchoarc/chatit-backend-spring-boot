package com.devit.chatapp.dto.request;

import com.devit.chatapp.enums.ConversationType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
public class CreateConversationRequest {

    //@ValueOfEnum(enumClass = ConversationType.class, message = "Tipo de conversación inválida")
    private ConversationType type;

    private String name;

    //@NotEmpty(message = "Debe haber al menos un miembro")
    private Set<String> memberIds;
}
