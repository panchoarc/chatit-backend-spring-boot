package com.devit.chatapp.service;

import com.devit.chatapp.dto.request.UserRegisterDTO;
import com.devit.chatapp.entity.Role;
import com.devit.chatapp.entity.User;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Set;

public interface UserService {

    User getUserByKeycloakId(String keycloakId);

    String extractKeycloakIdFromUser(Jwt user);

    void saveUserToDatabase(UserRegisterDTO userDTO, Role role, String keycloakId);

    boolean userExistsInDatabase(String email, String username);

    boolean findByUserName(String userName);

    User findById(Long creatorId);

    Set<User> findAllByKeycloakIds(Set<String> memberIds);
}
