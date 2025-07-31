package com.devit.chatapp.service;

import com.devit.chatapp.dto.request.ChatMessageRequest;
import com.devit.chatapp.dto.response.ChatMessageResponse;
import com.devit.chatapp.entity.Conversation;
import org.springframework.data.domain.Slice;

public interface MessageService {

    ChatMessageResponse saveMessage(Conversation conversation, ChatMessageRequest message);

    Slice<ChatMessageResponse> getMessagesByChatId(Long chatId, Long page, Long size);

    long countUnreadMessages(Long conversationId,String keycloakId);

}
