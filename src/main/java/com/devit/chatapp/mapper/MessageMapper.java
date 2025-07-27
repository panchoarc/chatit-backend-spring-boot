package com.devit.chatapp.mapper;

import com.devit.chatapp.dto.response.ChatMessageResponse;
import com.devit.chatapp.entity.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MessageMapper {

    @Mapping(target = "senderId", expression = "java(message.getSender().getKeycloakId())")
    ChatMessageResponse toChatMessageResponseDTO(Message message);
}
