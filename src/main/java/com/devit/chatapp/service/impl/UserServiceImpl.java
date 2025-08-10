package com.devit.chatapp.service.impl;

import com.devit.chatapp.dto.request.ProfileUpdateRequest;
import com.devit.chatapp.dto.request.UserRegisterDTO;
import com.devit.chatapp.entity.Role;
import com.devit.chatapp.entity.User;
import com.devit.chatapp.exception.ResourceNotFoundException;
import com.devit.chatapp.repository.UserRepository;
import com.devit.chatapp.service.FileService;
import com.devit.chatapp.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final FileService fileService;

    @Override
    public User getUserByKeycloakId(String keycloakId) {
        return userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Override
    public void saveUserToDatabase(UserRegisterDTO userDTO, Role role, String keycloakId) {
        User newUser = new User();
        newUser.setEmail(userDTO.getEmail());
        newUser.setFirstName(userDTO.getFirstName());
        newUser.setUsername(userDTO.getUserName());
        newUser.setLastName(userDTO.getLastName());
        newUser.setKeycloakId(keycloakId);
        newUser.setRole(role);
        userRepository.save(newUser);
    }

    @Override
    public boolean userExistsInDatabase(String email, String username) {
        return userRepository.findByEmailOrUsername(email, username).isPresent();

    }

    @Override
    public boolean findByUserName(String userName) {
        return userRepository.findByUsername(userName).isPresent();
    }

    @Override
    public Set<User> findAllByKeycloakIds(Set<String> memberIds) {
        return userRepository.findAllMembersIds(memberIds);
    }

    @Override
    public void updateUserProfile(ProfileUpdateRequest user, MultipartFile avatar, String userKeycloakId) {

        User updatedUser =  getUserByKeycloakId(userKeycloakId);

        updatedUser.setEmail(user.getEmail());
        updatedUser.setUsername(user.getUsername());
        if(avatar!=null){
            String avatarUrl = fileService.uploadFile(avatar);
            updatedUser.setAvatarUrl(avatarUrl);
        }
        userRepository.save(updatedUser);


    }
}
