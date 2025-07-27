package com.devit.chatapp.service.impl;

import com.devit.chatapp.dto.request.ChatMessageRequest;
import com.devit.chatapp.dto.response.ChatMessageResponse;
import com.devit.chatapp.entity.Conversation;
import com.devit.chatapp.entity.Message;
import com.devit.chatapp.entity.User;
import com.devit.chatapp.mapper.MessageMapper;
import com.devit.chatapp.repository.MessageRepository;
import com.devit.chatapp.service.ConversationService;
import com.devit.chatapp.service.MessageService;
import com.devit.chatapp.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class MessageServiceImpl implements MessageService {


    private final ConversationService conversationService;
    private final MessageRepository messageRepo;
    private final UserService userService;
    private final MessageMapper messageMapper;


    @Override
    public ChatMessageResponse saveMessage(Long chatId, ChatMessageRequest chatMessage) {
        Conversation conversation = conversationService.findById(chatId);

        User sender = userService.getUserByKeycloakId(chatMessage.getSenderId());

        Message message = new Message();
        message.setConversation(conversation);
        message.setSender(sender);
        message.setContent(chatMessage.getContent());
        message.setType(chatMessage.getType());
        Message save = messageRepo.save(message);
        return messageMapper.toChatMessageResponseDTO(save);
    }

    @Override
    @Transactional
    public Slice<ChatMessageResponse> getMessagesByChatId(Long conversationId, Long page, Long size) {

        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(Math.toIntExact(page), Math.toIntExact(size), sort);

        Slice<Message> pagedConversations = messageRepo.findByConversationId(conversationId, pageable);
        return pagedConversations.map(messageMapper::toChatMessageResponseDTO);

    }
}
