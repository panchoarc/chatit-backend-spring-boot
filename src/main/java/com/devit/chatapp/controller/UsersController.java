package com.devit.chatapp.controller;


import com.devit.chatapp.dto.request.ProfileUpdateRequest;
import com.devit.chatapp.dto.response.UserInfoResponse;
import com.devit.chatapp.entity.User;
import com.devit.chatapp.service.PresenceService;
import com.devit.chatapp.service.UserService;
import com.devit.chatapp.util.ResponseAPI;
import com.devit.chatapp.util.ResponseBuilder;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UsersController {

    private final UserService userService;
    private final PresenceService presenceService;

    @GetMapping("/me")
    public ResponseAPI<UserInfoResponse> getCurrentUser(@AuthenticationPrincipal Jwt jwt) {
        User myUser = userService.getUserByKeycloakId(jwt.getSubject());

        UserInfoResponse userInfoResponse = new UserInfoResponse();
        userInfoResponse.setEmail(myUser.getEmail());
        userInfoResponse.setFirstName(myUser.getFirstName());
        userInfoResponse.setLastName(myUser.getLastName());
        userInfoResponse.setUserName(myUser.getUsername());
        userInfoResponse.setAvatarUrl(myUser.getAvatarUrl());
        userInfoResponse.setKeycloakId(myUser.getKeycloakId());
        userInfoResponse.setRoles(myUser.getRole().getName());

        return ResponseBuilder.success("User found successfully", userInfoResponse);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseAPI<Void> updateProfile(
            @Valid @RequestPart("data") ProfileUpdateRequest dto,
            @RequestPart(value = "avatar", required = false) MultipartFile avatar,
            @AuthenticationPrincipal Jwt jwt
    ) {
        String keycloakId = jwt.getSubject();
        userService.updateUserProfile(dto, avatar, keycloakId);
        return ResponseBuilder.success("Perfil actualizado correctamente", null);
    }


    @GetMapping("/{username}/status")
    public UserStatusDto getStatus(@PathVariable String username) {
        boolean online = presenceService.isUserOnline(username);
        LocalDateTime lastSeen = presenceService.getLastSeen(username);
        return new UserStatusDto(username, online, lastSeen);
    }


    public record UserStatusDto(String userId, boolean online, LocalDateTime lastSeen) {

    }

}
