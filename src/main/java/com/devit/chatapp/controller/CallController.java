package com.devit.chatapp.controller;


import com.devit.chatapp.dto.CallMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class CallController {

    private final SimpMessagingTemplate messagingTemplate;


    @MessageMapping("/call/send")
    public void handleCall(CallMessage message) {
        log.info("Enviando llamada de {} a {} con tipo {}", message.getSenderId(), message.getReceiverId(), message.getType());
        messagingTemplate.convertAndSendToUser(
                message.getReceiverId(),
                "/queue/call",
                message
        );
    }

}
