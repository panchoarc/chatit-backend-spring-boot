package com.devit.chatapp.service;

import com.devit.chatapp.dto.request.ChatMessageRequest;

public interface ChatService {

    void sendMessage(Long chatId, ChatMessageRequest request);

}
