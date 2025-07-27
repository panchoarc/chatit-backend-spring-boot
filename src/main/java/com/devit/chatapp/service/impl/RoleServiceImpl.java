package com.devit.chatapp.service.impl;

import com.devit.chatapp.entity.Role;
import com.devit.chatapp.repository.RoleRepository;
import com.devit.chatapp.service.KeycloakService;
import com.devit.chatapp.service.RoleService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final KeycloakService keycloakService;

    @Override
    public void syncKeycloakRoles() {
        List<RoleRepresentation> roles = keycloakService.getClientRoles();
        for (RoleRepresentation role : roles) {
            String roleId = role.getId();
            String roleName = role.getName();

            boolean isValidRole = roleName.contains("uma_protection");
            if(!isValidRole){
                syncRoleWithDatabase(roleId, roleName);
            }
        }
    }

    @Override
    public Role findByName(String name) {
        return roleRepository.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException("Role with name " + name + " not found"));
    }

    private void syncRoleWithDatabase(String roleId, String roleName) {
        Role existingRole = roleRepository.findByExternalId(roleId);

        if (existingRole == null) {
            Role newRole = new Role();
            newRole.setExternalId(roleId);
            newRole.setName(roleName);
            newRole.setActive(true);
            roleRepository.save(newRole);
        } else {
            if (!existingRole.getName().equals(roleName)) {
                existingRole.setName(roleName);
                roleRepository.save(existingRole);
            }
        }
    }
}
