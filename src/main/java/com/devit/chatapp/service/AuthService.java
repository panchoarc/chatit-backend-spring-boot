package com.devit.chatapp.service;

import com.devit.chatapp.dto.request.UserRegisterDTO;
import com.devit.chatapp.dto.response.UserLoginDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.keycloak.representations.AccessTokenResponse;

public interface AuthService {

    void createUser(UserRegisterDTO userRegisterDTO);

    AccessTokenResponse login(UserLoginDTO userLoginDTO) throws JsonProcessingException;

    String loginWithProvider(String provider,String redirectUrl);

    AccessTokenResponse handleAuthCallback(String code, String redirectUrl) throws JsonProcessingException;


    AccessTokenResponse refreshAccessToken(String refreshToken);
}
