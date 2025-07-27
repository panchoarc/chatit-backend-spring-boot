package com.devit.chatapp.service;

import com.devit.chatapp.dto.request.UserRegisterDTO;
import org.keycloak.representations.idm.RoleRepresentation;

import java.util.List;

public interface KeycloakService {

    List<RoleRepresentation> getClientRoles();

    String getClientInternalId();

    String getClientId();

    String getClientSecret();

    String createUserInKeycloak(UserRegisterDTO userRegisterDTO);

    void assignDefaultRoleToUser(String keycloakUserId, String roleName);

    void sendKeycloakVerifyEmail(String keycloakId);

    String getServerToken();

    void deleteUserFromKeycloak(String userId);

    String getServerUrl();

    String getAuthUrl();

    boolean isProviderEnabled(String providerAlias);

    String getRedirectProvider(String provider, String redirectUrl);
}