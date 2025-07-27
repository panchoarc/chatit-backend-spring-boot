package com.devit.chatapp.config;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class JwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    @Value("${jwt.auth.converter.resource-id}")
    private String resourceId;

    @Value("${jwt.auth.converter.principal-attribute}")
    private String principalAttribute;

    private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

    @Override
    public JwtAuthenticationToken convert(@NonNull Jwt jwt) {
        try {
            Set<GrantedAuthority> authorities = new HashSet<>(jwtGrantedAuthoritiesConverter.convert(jwt));
            authorities.addAll(extractResourceRoles(jwt));

            log.debug("JWT converted successfully for user: {}, authorities: {}",
                    getPrincipalName(jwt), authorities);

            return new JwtAuthenticationToken(jwt, authorities, getPrincipalName(jwt));
        } catch (Exception e) {
            log.error("Error converting JWT: {}", e.getMessage());
            throw new RuntimeException("Error processing JWT token", e);
        }
    }

    private String getPrincipalName(Jwt jwt) {
        String claimName = JwtClaimNames.SUB;
        if (principalAttribute != null) {
            claimName = principalAttribute;
        }
        return jwt.getClaim(claimName);
    }

    public Collection<GrantedAuthority> extractResourceRoles(Jwt jwt) {
        try {
            // Obtener el mapa de acceso a recursos del token JWT
            Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
            if (resourceAccess == null) {
                log.debug("No resource_access claim found in JWT");
                return Set.of();
            }

            // Obtener el recurso específico
            Object resource = resourceAccess.get(resourceId);
            if (resource == null) {
                log.debug("Resource '{}' not found in resource_access", resourceId);
                return Set.of();
            }

            // Verificar que el recurso sea un Map
            if (!(resource instanceof Map)) {
                log.warn("Resource '{}' is not a Map: {}", resourceId, resource.getClass());
                return Set.of();
            }

            Map<String, Object> resourceMap = (Map<String, Object>) resource;

            // Obtener los roles del recurso específico
            Object rolesObj = resourceMap.get("roles");
            if (rolesObj == null) {
                log.debug("No roles found for resource '{}'", resourceId);
                return Set.of();
            }

            // Verificar que roles sea una Collection
            if (!(rolesObj instanceof Collection)) {
                log.warn("Roles for resource '{}' is not a Collection: {}", resourceId, rolesObj.getClass());
                return Set.of();
            }

            Collection<String> resourceRoles = (Collection<String>) rolesObj;

            // Transformar los roles en GrantedAuthority con prefijo "ROLE_"
            Set<GrantedAuthority> authorities = resourceRoles.stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                    .collect(Collectors.toSet());

            log.debug("Extracted roles for resource '{}': {}", resourceId, authorities);
            return authorities;

        } catch (Exception e) {
            log.error("Error extracting resource roles: {}", e.getMessage());
            return Set.of();
        }
    }
}