package com.devit.chatapp.repository;

import com.devit.chatapp.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findByExternalId(String roleId);

    Optional<Role> findByName(String name);
}
