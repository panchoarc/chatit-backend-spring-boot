package com.devit.chatapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDTO {
    private Long conversationId;   // a qué conversación pertenece
    private String content;        // contenido del mensaje (texto, base64, etc.)
    private String type;           // TEXT, FILE, IMAGE, etc.
    private String fileName;       // opcional si es un archivo
    private String fileType;       // MIME type (ej: image/png)
}