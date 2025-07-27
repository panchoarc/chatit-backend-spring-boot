package com.devit.chatapp.controller;

import com.devit.chatapp.dto.request.ChatMessageRequest;
import com.devit.chatapp.dto.request.TypingNotification;
import com.devit.chatapp.dto.response.ChatMessageResponse;
import com.devit.chatapp.dto.response.ChatUpdatesDTO;
import com.devit.chatapp.service.ConversationService;
import com.devit.chatapp.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.Instant;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final MessageService messageService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ConversationService conversationService;

    @MessageMapping("/chat/{chatId}")
    public void sendMessage(@DestinationVariable Long chatId, ChatMessageRequest messageRequest) {

        messageRequest.setTimestamp(Instant.now());
        ChatMessageResponse messageResponse = messageService.saveMessage(chatId, messageRequest);
        simpMessagingTemplate.convertAndSend("/topic/chat/" + chatId, messageResponse);

        ChatUpdatesDTO updatesResponse = new ChatUpdatesDTO();
        updatesResponse.setChatId(chatId);
        updatesResponse.setLastMessage(messageResponse);

        List<String> members = conversationService.findMembersByChatId(chatId);

        for (String member : members) {
            simpMessagingTemplate.convertAndSendToUser(member, "/queue/chat-updates", updatesResponse);
        }
    }

    @MessageMapping("/typing")
    public void handleTyping(@Payload TypingNotification notification) {
        simpMessagingTemplate.convertAndSend(
                "/topic/typing/" + notification.getChatId(),
                notification
        );
    }

}
