package com.devit.chatapp.service.impl;

import com.devit.chatapp.dto.ConversationWithLastMessageDTO;
import com.devit.chatapp.dto.request.AddMemberRequest;
import com.devit.chatapp.dto.request.CreateConversationRequest;
import com.devit.chatapp.dto.response.ChatMessageResponse;
import com.devit.chatapp.dto.response.ConversationResponseDTO;
import com.devit.chatapp.dto.response.UserResponseDTO;
import com.devit.chatapp.entity.Conversation;
import com.devit.chatapp.entity.ConversationMember;
import com.devit.chatapp.entity.User;
import com.devit.chatapp.enums.ConversationType;
import com.devit.chatapp.enums.MessageType;
import com.devit.chatapp.exception.ResourceNotFoundException;
import com.devit.chatapp.mapper.ConversationMapper;
import com.devit.chatapp.mapper.UserMapper;
import com.devit.chatapp.repository.ConversationRepository;
import com.devit.chatapp.service.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConversationServiceImpl implements ConversationService {

    private final ConversationRepository conversationRepository;
    private final UserService userService;
    private final ConversationMapper conversationMapper;
    private final ChatNotificationService chatNotificationService;

    private final UserMapper userMapper;

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ConversationMemberService conversationMemberService;
    private final MessageService messageService;

    @Override
    public Conversation findById(Long conversationId) {
        return conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation" + conversationId + "not found"));
    }

    @Override
    @Transactional
    public void createConversation(CreateConversationRequest conversationRequest, String creatorId) {

        Set<User> members = userService.findAllByKeycloakIds(conversationRequest.getMemberIds());

        if (members.size() < conversationRequest.getMemberIds().size()) {
            throw new IllegalArgumentException("Uno o más usuarios no existen.");
        }

        if (conversationRequest.getType() == ConversationType.GROUP) {
            if (conversationRequest.getName() == null || conversationRequest.getName().isBlank()) {
                throw new IllegalArgumentException("El nombre del grupo es obligatorio para conversaciones de tipo GROUP.");
            }

        } else if (conversationRequest.getType() == ConversationType.DM && members.size() != 2) {
            throw new IllegalArgumentException("Una conversación DM debe incluir exactamente un usuario adicional al creador.");
        }

        Conversation conversation = new Conversation();
        conversation.setType(conversationRequest.getType());
        conversation.setName(conversationRequest.getType() == ConversationType.GROUP ? conversationRequest.getName() : null);

        Set<User> allParticipants = new HashSet<>(members);

        Set<ConversationMember> memberEntities = allParticipants.stream()
                .map(user -> {
                    ConversationMember cm = new ConversationMember();
                    cm.setUser(user);
                    cm.setConversation(conversation); // muy importante
                    cm.setLastReadAt(null); // opcionalmente: Instant.now() para el creador
                    return cm;
                })
                .collect(Collectors.toSet());

        conversation.setMembers(memberEntities);

        Conversation saved = conversationRepository.save(conversation);

        ConversationResponseDTO conversationResponseDTO = conversationMapper.toConversationResponseDTO(saved, creatorId);
        for (User member : allParticipants) {
            simpMessagingTemplate.convertAndSendToUser(member.getKeycloakId(), "/queue/chats", conversationResponseDTO);
        }
    }

    @Override
    @Transactional
    public List<ConversationResponseDTO> findMyConversations(String keycloakId) {
        List<ConversationWithLastMessageDTO> myConversations = conversationRepository.findConversationsWithLastMessageByKeycloakId(keycloakId);


        return myConversations.stream()
                .map(dto -> {

                    ConversationResponseDTO response = new ConversationResponseDTO();
                    response.setId(dto.getConversationId());
                    response.setName(dto.getName());
                    response.setType(ConversationType.valueOf(dto.getType()));
                    response.setAvatarUrl(dto.getAvatar());

                    if (dto.getLastMessageId() != null) {
                        ChatMessageResponse lastMessage = new ChatMessageResponse();
                        lastMessage.setId(dto.getLastMessageId());
                        lastMessage.setContent(dto.getLastMessageContent());
                        lastMessage.setType(MessageType.valueOf(dto.getLastMessageType()));
                        lastMessage.setCreatedAt(dto.getLastMessageCreatedAt());
                        lastMessage.setSenderId(dto.getLastMessageSenderId());
                        response.setLastMessage(lastMessage);
                    }
                    long unreadMessages = messageService.countUnreadMessages(dto.getConversationId(), keycloakId);
                    response.setUnreadCount(unreadMessages);

                    return response;
                })
                .toList();

    }

    @Override
    @Transactional
    public List<UserResponseDTO> getMembersByChatId(Long chatId) {
        Conversation conv = findById(chatId);

        List<ConversationMember> members = conversationMemberService.getConversationMembers(conv.getId());

        return members.stream()
                .map(ConversationMember::getUser)
                .map(userMapper::toUserResponseDTO)
                .toList();
    }

    @Override
    @Transactional
    public void addMemberToConversation(Long conversationId, AddMemberRequest memberRequest) {
        Conversation conversation = findById(conversationId);

        List<ConversationMember> members = conversationMemberService.getConversationMembers(conversationId);
        User newUser = userService.getUserByKeycloakId(memberRequest.getMemberId());

        boolean alreadyMember = members.stream()
                .anyMatch(cm -> cm.getUser().getId().equals(newUser.getId()));

        if (alreadyMember) {
            throw new IllegalArgumentException("El usuario ya es miembro de la conversación.");
        }

        ConversationMember conversationMember = new ConversationMember();
        conversationMember.setConversation(conversation);
        conversationMember.setUser(newUser);
        conversationMember.setLastReadAt(Instant.now());

        conversation.getMembers().add(conversationMember);
        conversationRepository.save(conversation);


        ConversationResponseDTO conversationResponseDTO =
                conversationMapper.toConversationResponseDTO(conversation, memberRequest.getMemberId());

        chatNotificationService.notifyUserOfNewChat(memberRequest.getMemberId(), conversationResponseDTO);
    }

    @Override
    public List<String> findMembersByChatId(Long chatId) {
        return conversationRepository.findMemberKeycloakIdsByChatId(chatId);
    }
}
