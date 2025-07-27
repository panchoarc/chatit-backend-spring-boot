package com.devit.chatapp.service.impl;

import com.devit.chatapp.dto.request.UserRegisterDTO;
import com.devit.chatapp.dto.response.UserLoginDTO;
import com.devit.chatapp.entity.Role;
import com.devit.chatapp.exception.AuthenticationException;
import com.devit.chatapp.exception.KeycloakIntegrationException;
import com.devit.chatapp.exception.ResourceExistException;
import com.devit.chatapp.exception.ResourceNotFoundException;
import com.devit.chatapp.service.AuthService;
import com.devit.chatapp.service.KeycloakService;
import com.devit.chatapp.service.RoleService;
import com.devit.chatapp.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final KeycloakService keycloakService;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final RoleService roleService;

    @Value("${keycloak.email-verified}")
    private boolean emailVerified;

    @Override
    public void createUser(UserRegisterDTO userRegisterDTO) {

        log.info("createUser: {}", userRegisterDTO);

        String queriedRole = userRegisterDTO.getRole() != null ? userRegisterDTO.getRole() : "user";

        Role role = roleService.findByName(queriedRole);

        boolean userExists = userService.userExistsInDatabase(userRegisterDTO.getEmail(), userRegisterDTO.getUserName());

        if (userExists) {
            throw new ResourceExistException("Email or username is already in use");
        }

        String keycloakUserId = keycloakService.createUserInKeycloak(userRegisterDTO);

        try {
            keycloakService.assignDefaultRoleToUser(keycloakUserId, role.getName());
            userService.saveUserToDatabase(userRegisterDTO, role, keycloakUserId);
            if (!emailVerified) {
                keycloakService.sendKeycloakVerifyEmail(keycloakUserId);
            }
        } catch (KeycloakIntegrationException e) {
            log.error("Failed to create user in Keycloak {}. {}", keycloakUserId, e.getMessage());
            keycloakService.deleteUserFromKeycloak(keycloakUserId);
        }
    }

    @Override
    public AccessTokenResponse login(UserLoginDTO userLoginDTO) throws JsonProcessingException {

        boolean userFound = userService.findByUserName(userLoginDTO.getUserName());
        if (!userFound) {
            throw new ResourceNotFoundException("User not found");
        }

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "password");
        form.add("client_id", keycloakService.getClientId());
        form.add("client_secret", keycloakService.getClientSecret());
        form.add("username", userLoginDTO.getUserName());
        form.add("password", userLoginDTO.getPassword());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(form, headers);
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    keycloakService.getServerToken(),
                    entity,
                    String.class
            );
            return objectMapper.readValue(response.getBody(), AccessTokenResponse.class);
        } catch (HttpClientErrorException.Unauthorized e) {
            log.error("Excepción {}", e.getMessage());
            throw new AuthenticationException("Invalid credentials");
        } catch (HttpClientErrorException e) {
            throw new AuthenticationException("Login failed. Details: " + e.getResponseBodyAsString());
        }
    }

    @Override
    public String loginWithProvider(String provider, String redirectUrl) {

        boolean isProviderEnabled = keycloakService.isProviderEnabled(provider);

        if (!isProviderEnabled) {
            throw new ResourceNotFoundException("Provider " + provider + " is not enabled");
        }

        return keycloakService.getRedirectProvider(provider, redirectUrl);
    }

    @Override
    public AccessTokenResponse handleAuthCallback(String code, String redirectUrl) throws JsonProcessingException {

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "authorization_code");
        form.add("client_id", keycloakService.getClientId());
        form.add("client_secret", keycloakService.getClientSecret());
        form.add("code", code);
        form.add("redirect_uri", redirectUrl);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(form, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    keycloakService.getServerToken(),
                    entity,
                    String.class
            );

            String responseBody = response.getBody();
            return objectMapper.readValue(responseBody, AccessTokenResponse.class);

        } catch (HttpClientErrorException.Unauthorized e) {
            throw new AuthenticationException("Invalid credentials");
        } catch (HttpClientErrorException e) {
            log.info("Keycloak login failed: {}", e.getResponseBodyAsString());
            throw new AuthenticationException("Login failed. Details: " + e.getResponseBodyAsString());
        }
    }

    @Override
    public AccessTokenResponse refreshAccessToken(String refreshToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "refresh_token");
        params.add("refresh_token", refreshToken);
        params.add("client_id", keycloakService.getClientId());
        params.add("client_secret", keycloakService.getClientSecret());

        try {

            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers);
            ResponseEntity<AccessTokenResponse> response = restTemplate.postForEntity(
                    keycloakService.getServerToken(), entity, AccessTokenResponse.class
            );

            return response.getBody();

        } catch (HttpClientErrorException.Unauthorized e) {
            throw new AuthenticationException("Invalid credentials");
        } catch (HttpClientErrorException e) {
            log.error("Keycloak login failed: {}", e.getResponseBodyAsString());
            throw new AuthenticationException("Login failed. Details: " + e.getResponseBodyAsString());
        }
    }
}