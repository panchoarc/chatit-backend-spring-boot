package com.devit.chatapp.service;

import com.devit.chatapp.dto.request.ChatMessageRequest;
import com.devit.chatapp.dto.response.ChatMessageResponse;
import org.springframework.data.domain.Slice;

public interface MessageService {

    ChatMessageResponse saveMessage(Long chatId, ChatMessageRequest message);

    Slice<ChatMessageResponse> getMessagesByChatId(Long chatId, Long page, Long size);

}
