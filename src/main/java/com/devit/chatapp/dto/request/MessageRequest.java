package com.devit.chatapp.dto.request;

import com.devit.chatapp.enums.MessageType;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MessageRequest {

    private String content;
    private MessageType type;

}
