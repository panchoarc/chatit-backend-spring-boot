package com.devit.chatapp.repository;

import com.devit.chatapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, String> {


    Optional<User> findById(Long id);

    Optional<User> findByKeycloakId(String keycloakId);

    Optional<User> findByEmailOrUsername(String email, String username);

    Optional<User> findByUsername(String username);


    @Query("""
                SELECT u FROM User u
                WHERE u.keycloakId IN :memberIds
            """)
    Set<User> findAllMembersIds(@Param("memberIds") Set<String> keycloakMembersIds);


}
