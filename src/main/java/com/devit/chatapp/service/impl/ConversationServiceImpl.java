package com.devit.chatapp.service.impl;

import com.devit.chatapp.dto.ConversationWithLastMessageDTO;
import com.devit.chatapp.dto.request.AddMemberRequest;
import com.devit.chatapp.dto.request.CreateConversationRequest;
import com.devit.chatapp.dto.response.ChatMessageResponse;
import com.devit.chatapp.dto.response.ConversationResponseDTO;
import com.devit.chatapp.dto.response.UserResponseDTO;
import com.devit.chatapp.entity.Conversation;
import com.devit.chatapp.entity.User;
import com.devit.chatapp.enums.ConversationType;
import com.devit.chatapp.enums.MessageType;
import com.devit.chatapp.exception.ResourceNotFoundException;
import com.devit.chatapp.mapper.ConversationMapper;
import com.devit.chatapp.mapper.UserMapper;
import com.devit.chatapp.repository.ConversationRepository;
import com.devit.chatapp.service.ChatNotificationService;
import com.devit.chatapp.service.ConversationService;
import com.devit.chatapp.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

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

    @Override
    public Conversation findById(Long conversationId) {
        return conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation" + conversationId + "not found"));
    }

    public boolean isMember(Long conversationId, Long userId) {
        return conversationRepository.existsByIdAndMembers_Id(conversationId, userId);
    }

    @Override
    @Transactional
    public void createConversation(CreateConversationRequest conversationRequest, String creatorId) {
        if (conversationRequest.getType() == null) {
            throw new IllegalArgumentException("El tipo de conversación es obligatorio.");
        }

        if (conversationRequest.getMemberIds() == null || conversationRequest.getMemberIds().isEmpty()) {
            throw new IllegalArgumentException("Se debe especificar al menos un miembro.");
        }

        User creator = userService.getUserByKeycloakId(creatorId);
        Set<User> members = userService.findAllByKeycloakIds(conversationRequest.getMemberIds());

        if (members.size() < conversationRequest.getMemberIds().size()) {
            throw new IllegalArgumentException("Uno o más usuarios no existen.");
        }

        if (conversationRequest.getType() == ConversationType.GROUP) {
            if (conversationRequest.getName() == null || conversationRequest.getName().isBlank()) {
                throw new IllegalArgumentException("El nombre del grupo es obligatorio para conversaciones de tipo GROUP.");
            }

            if (members.isEmpty() || members.contains(creator)) {
                throw new IllegalArgumentException("Un grupo debe tener al menos un miembro adicional al creador.");
            }

            members.add(creator);

        } else if (conversationRequest.getType() == ConversationType.DM) {
            // Asegurar que hay exactamente un usuario más aparte del creador
            if (members.size() != 1 || members.contains(creator)) {
                throw new IllegalArgumentException("Una conversación DM debe incluir exactamente un usuario adicional al creador.");
            }

            // Crear un set temporal para la verificación
            User otherUser = members.iterator().next(); // porque debe haber solo uno

            Optional<Conversation> existingDm = conversationRepository.findDmByMembers(creator, otherUser);

            if (existingDm.isPresent()) {
                throw new IllegalStateException("Ya existe una conversación DM entre estos usuarios.");
            }

            // Agregar al creador como miembro
            members.add(creator);
        } else {
            throw new IllegalArgumentException("Tipo de conversación no soportado.");
        }

        Conversation conversation = new Conversation();
        conversation.setType(conversationRequest.getType());
        conversation.setCreator(creator);
        conversation.setName(conversationRequest.getType() == ConversationType.GROUP ? conversationRequest.getName() : null);
        conversation.setMembers(members);


        Conversation save = conversationRepository.save(conversation);
        ConversationResponseDTO conversationResponseDTO = conversationMapper.toConversationResponseDTO(save, creatorId);

        for (User member : members) {
            simpMessagingTemplate.convertAndSendToUser(member.getKeycloakId(), "/queue/chats", conversationResponseDTO
            );
        }
    }

    @Override
    @Transactional
    public List<ConversationResponseDTO> findMyConversations(String keycloakId) {
        /*
         *Pasos necesarios
         * 1.- Obtener mis conversaciones ordenadas
         *
         * */
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

                    return response;
                })
                .toList();

    }

    @Override
    @Transactional
    public List<UserResponseDTO> getMembersByChatId(Long chatId) {
        Conversation conv = findById(chatId);

        return conv.getMembers().stream()
                .map(userMapper::toUserResponseDTO)
                .toList();
    }

    @Override
    @Transactional
    public void addMemberToConversation(Long conversationId, AddMemberRequest memberRequest) {
        Conversation conversation = findById(conversationId);
        User newUser = userService.getUserByKeycloakId(memberRequest.getMemberId());

        conversation.getMembers().add(newUser);
        conversationRepository.save(conversation);

        ConversationResponseDTO conversationResponseDTO = conversationMapper.toConversationResponseDTO(conversation, memberRequest.getMemberId());
        chatNotificationService.notifyUserOfNewChat(memberRequest.getMemberId(), conversationResponseDTO);
    }

    @Override
    public List<String> findMembersByChatId(Long chatId) {
        return conversationRepository.findMemberKeycloakIdsByChatId(chatId);
    }
}
