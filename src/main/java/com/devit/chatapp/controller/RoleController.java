package com.devit.chatapp.controller;

import com.devit.chatapp.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @PostMapping("/sync")
    public void syncKeycloakRoles() {
        roleService.syncKeycloakRoles();
    }
}
