package com.devit.chatapp.controller;


import com.devit.chatapp.dto.response.UserInfoResponse;
import com.devit.chatapp.entity.User;
import com.devit.chatapp.service.UserService;
import com.devit.chatapp.util.ResponseAPI;
import com.devit.chatapp.util.ResponseBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UsersController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseAPI<UserInfoResponse> getCurrentUser(@AuthenticationPrincipal Jwt jwt) {
        User myUser = userService.getUserByKeycloakId(jwt.getSubject());

        UserInfoResponse userInfoResponse = new UserInfoResponse();
        userInfoResponse.setEmail(myUser.getEmail());
        userInfoResponse.setFirstName(myUser.getFirstName());
        userInfoResponse.setLastName(myUser.getLastName());
        userInfoResponse.setUserName(myUser.getUsername());
        userInfoResponse.setKeycloakId(myUser.getKeycloakId());
        userInfoResponse.setRoles(myUser.getRole().getName());

        return ResponseBuilder.success("User found successfully", userInfoResponse);
    }

}
