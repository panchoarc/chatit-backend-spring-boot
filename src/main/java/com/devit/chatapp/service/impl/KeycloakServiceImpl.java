package com.devit.chatapp.service.impl;

import com.devit.chatapp.dto.request.UserRegisterDTO;
import com.devit.chatapp.exception.KeycloakIntegrationException;
import com.devit.chatapp.exception.ResourceNotFoundException;
import com.devit.chatapp.service.KeycloakService;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.core.Response;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class KeycloakServiceImpl implements KeycloakService {

    private final Keycloak keycloak;

    @Getter
    @Value("${keycloak.realm.name}")
    private String realmName;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Getter
    @Value("${keycloak.server-url}")
    private String authServerUrl;

    @Value("${keycloak.email-verified}")
    private boolean emailVerified;

    @Override
    public List<RoleRepresentation> getClientRoles() {
        ClientRepresentation client = getClientRepresentation();
        return getRealmResource().clients().get(client.getId()).roles().list();

    }

    @Override
    public String getClientInternalId() {
        ClientRepresentation client = getClientRepresentation();
        return client.getId();
    }

    @Override
    public String getClientId() {
        ClientRepresentation client = getClientRepresentation();
        return client.getClientId();
    }

    @Override
    public String getClientSecret() {
        ClientRepresentation client = getClientRepresentation();
        return client.getSecret();
    }

    @Override
    public String createUserInKeycloak(UserRegisterDTO userRegisterDTO) {
        UserRepresentation keycloakUser = buildKeycloakUserRepresentation(userRegisterDTO);

        log.info("userRepresentation : {}", keycloakUser.getRealmRoles());
        try (Response response = getUsersResource().create(keycloakUser)) {
            log.info("Response : {}", response.getStatusInfo().getReasonPhrase());
            if (response.getStatus() != 201) {
                Map<String, String> respuesta = response.readEntity(Map.class);
                log.info("Respuesta: {}", respuesta);
                throw new KeycloakIntegrationException(respuesta.get("errorMessage"));
            }
            return extractUserIdFromResponse(response);
        }
        catch (Exception e){
            log.info("Exception : {}", e.getCause());
            log.error(e.getMessage());
            throw new KeycloakIntegrationException(e.getMessage());
        }
    }

    private String extractUserIdFromResponse(Response response) {
        String path = response.getLocation().getPath();
        int lastSlash = path.lastIndexOf('/');
        return (lastSlash != -1) ? path.substring(lastSlash + 1) : path;
    }

    @Override
    public void assignDefaultRoleToUser(String keycloakUserId, String userRole) {


        String clientInternalId = getClientInternalId();
        RoleRepresentation roleToAssign = getRoleFromKeycloak(userRole);

        UserResource userResource = getUsersResource().get(keycloakUserId);
        userResource.roles().clientLevel(clientInternalId).add(List.of(roleToAssign));
    }

    @Async("taskExecutor")
    @Override
    public void sendKeycloakVerifyEmail(String keycloakId) {
        try {
            if (!emailVerified) {
                getUsersResource().get(keycloakId).sendVerifyEmail();
            }
        } catch (InternalServerErrorException e) {
            log.error("Keycloak Error: {}", e.getMessage());
            throw new KeycloakIntegrationException(e.getMessage());
        }
    }

    @Override
    public String getServerUrl() {
        return getAuthServerUrl();
    }

    @Override
    public String getAuthUrl() {
        return getAuthServerUrl() + "/realms/" + getRealmName() + "/protocol/openid-connect/auth";
    }

    @Override
    public boolean isProviderEnabled(String providerAlias) {

        List<IdentityProviderRepresentation> providers = getRealmResource().identityProviders().findAll();
        for (IdentityProviderRepresentation provider : providers) {
            if (provider.getAlias().equals(providerAlias)) {
                return provider.isEnabled();
            }
        }
        return false;
    }

    @Override
    public String getRedirectProvider(String provider, String redirectUrl) {
        return getAuthUrl() +
                "?kc_idp_hint=" + provider +
                "&client_id=" + getClientId() +
                "&response_type=code" +
                "&redirect_uri=" + redirectUrl;
    }


    @Override
    public String getServerToken() {
        return getAuthServerUrl() + "/realms/" + getRealmName() + "/protocol/openid-connect/token";
    }

    @Override
    public void deleteUserFromKeycloak(String username) {

        String userId = "";
        UsersResource usersResource = getUsersResource();
        List<UserRepresentation> users = usersResource.search(username, true);
        try {
            if (!users.isEmpty()) {
                userId = users.get(0).getId();
                usersResource.delete(userId);
            }
        } catch (Exception e) {
            String errorMessage = String.format("Error al eliminar usuario en Keycloak. ID: %s, Error: %s",
                    userId, e.getMessage());
            log.error(errorMessage);
            throw new KeycloakIntegrationException(errorMessage);
        }
    }


    private RoleRepresentation getRoleFromKeycloak(String roleName) {
        return getClientRoles().stream()
                .filter(role -> role.getName().equals(roleName))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Role '" + roleName + "' not found"));
    }

    private UserRepresentation buildKeycloakUserRepresentation(UserRegisterDTO userRegisterDTO) {
        UserRepresentation keycloakUser = new UserRepresentation();
        keycloakUser.setUsername(userRegisterDTO.getUserName());
        keycloakUser.setEmail(userRegisterDTO.getEmail());
        keycloakUser.setFirstName(userRegisterDTO.getFirstName());
        keycloakUser.setLastName(userRegisterDTO.getLastName());
        keycloakUser.setEnabled(true);
        keycloakUser.setEmailVerified(emailVerified);

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(userRegisterDTO.getPassword());
        credential.setTemporary(false);
        keycloakUser.setCredentials(List.of(credential));
        return keycloakUser;
    }


    public RealmResource getRealmResource() {
        return keycloak.realm(realmName);
    }

    public UsersResource getUsersResource() {
        return getRealmResource().users();
    }

    public ClientRepresentation getClientRepresentation() {

        Optional<ClientRepresentation> client = getRealmResource()
                .clients()
                .findByClientId(clientId)
                .stream()
                .findFirst();

        if (client.isEmpty()) {
            throw new ResourceNotFoundException("Client with clientId '" + clientId + "' not found");
        }
        return client.get();
    }
}