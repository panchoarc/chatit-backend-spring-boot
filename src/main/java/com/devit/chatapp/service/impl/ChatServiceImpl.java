package com.devit.chatapp.service.impl;

import com.devit.chatapp.dto.request.ChatMessageRequest;
import com.devit.chatapp.dto.response.ChatMessageResponse;
import com.devit.chatapp.dto.response.ChatUpdatesDTO;
import com.devit.chatapp.entity.Conversation;
import com.devit.chatapp.service.ChatService;
import com.devit.chatapp.service.ConversationMemberService;
import com.devit.chatapp.service.ConversationService;
import com.devit.chatapp.service.MessageService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatServiceImpl implements ChatService {

    private final MessageService messageService;
    private final ConversationService conversationService;
    private final ConversationMemberService conversationMemberService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @Transactional
    @Override
    public void sendMessage(Long chatId, ChatMessageRequest request) {
        Instant now = Instant.now();
        request.setTimestamp(now);

        Conversation conversation = conversationService.findById(chatId);

        ChatMessageResponse savedMessage = messageService.saveMessage(conversation, request);

        // Enviar mensaje al topic
        simpMessagingTemplate.convertAndSend("/topic/chat." + chatId, savedMessage);

        // Actualizar el lastReadAt del que lo envió
        conversationMemberService.updatedLastMessage(chatId, request.getSenderId());

        // Obtener los otros miembros
        List<String> members = conversationService.findMembersByChatId(chatId);

        for (String member : members) {
            long unread = messageService.countUnreadMessages(chatId, member);
            ChatUpdatesDTO update = new ChatUpdatesDTO();
            update.setChatId(chatId);
            update.setLastMessage(savedMessage);
            update.setLastMessageTimestamp(now);
            update.setUnreadCount(unread);
            simpMessagingTemplate.convertAndSendToUser(member, "/queue/chat-updates", update);
        }
    }
}