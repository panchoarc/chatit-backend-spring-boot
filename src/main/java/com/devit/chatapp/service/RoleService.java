package com.devit.chatapp.service;

import com.devit.chatapp.entity.Role;

public interface RoleService {

    void syncKeycloakRoles();

    Role findByName(String name);
}