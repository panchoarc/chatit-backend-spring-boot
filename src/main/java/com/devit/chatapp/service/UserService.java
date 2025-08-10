package com.devit.chatapp.service;

import com.devit.chatapp.dto.request.ProfileUpdateRequest;
import com.devit.chatapp.dto.request.UserRegisterDTO;
import com.devit.chatapp.entity.Role;
import com.devit.chatapp.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

public interface UserService {

    User getUserByKeycloakId(String keycloakId);

    void saveUserToDatabase(UserRegisterDTO userDTO, Role role, String keycloakId);

    boolean userExistsInDatabase(String email, String username);

    boolean findByUserName(String userName);

    Set<User> findAllByKeycloakIds(Set<String> memberIds);

    void updateUserProfile(ProfileUpdateRequest user, MultipartFile avatar, String keycloakId);
}
