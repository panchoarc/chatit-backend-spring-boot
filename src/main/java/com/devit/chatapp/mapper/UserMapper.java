package com.devit.chatapp.mapper;

import com.devit.chatapp.dto.response.UserResponseDTO;
import com.devit.chatapp.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponseDTO toUserResponseDTO(User user);
}