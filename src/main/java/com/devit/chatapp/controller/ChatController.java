package com.devit.chatapp.controller;

import com.devit.chatapp.dto.request.ChatMessageRequest;
import com.devit.chatapp.dto.request.TypingNotification;
import com.devit.chatapp.service.ChatService;
import com.devit.chatapp.service.ConversationMemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ChatService chatService;
    private final ConversationMemberService conversationMemberService;

    @MessageMapping("/chat.{chatId}")
    public void sendMessage(@DestinationVariable Long chatId, ChatMessageRequest messageRequest) {

        chatService.sendMessage(chatId, messageRequest);
    }

    @MessageMapping("/typing")
    public void handleTyping(@Payload TypingNotification notification) {
        simpMessagingTemplate.convertAndSend(
                "/topic/typing." + notification.getChatId(),
                notification
        );
    }

    @MessageMapping("/chat.{chatId}.read")
    public void markAsRead(@DestinationVariable Long chatId, Principal principal) {

        conversationMemberService.markAsRead(chatId, principal.getName());
        
        simpMessagingTemplate.convertAndSendToUser(
                principal.getName(),
                "/queue/chat-read",
                Map.of("chatId", chatId)
        );
    }


}
