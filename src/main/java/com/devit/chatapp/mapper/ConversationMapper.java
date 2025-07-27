package com.devit.chatapp.mapper;


import com.devit.chatapp.dto.response.ConversationResponseDTO;
import com.devit.chatapp.entity.Conversation;
import com.devit.chatapp.entity.User;
import com.devit.chatapp.enums.ConversationType;
import org.mapstruct.*;

import java.util.Objects;

@Mapper(componentModel = "spring", uses = MessageMapper.class)
public interface ConversationMapper {

    @Mapping(target = "name", ignore = true)
    @Mapping(target = "avatarUrl", ignore = true)
    ConversationResponseDTO toConversationResponseDTO(Conversation conversation, @Context String currentUserId);

    @AfterMapping
    default void fillDynamicFields(
            Conversation conversation,
            @MappingTarget ConversationResponseDTO dto,
            @Context String currentUserId
    ) {
        if (conversation.getType() == ConversationType.GROUP) {
            dto.setName(conversation.getName() != null ? conversation.getName() : "Grupo sin nombre");
            dto.setAvatarUrl("/images/group-avatar.png");
        } else {
            User other = conversation.getMembers().stream()
                    .filter(m -> !m.getKeycloakId().equals(currentUserId))
                    .findFirst()
                    .orElse(null);

            dto.setName(Objects.requireNonNull(other).getFirstName() + " " + other.getLastName());
            dto.setAvatarUrl(other.getAvatarUrl());
        }
    }
}

