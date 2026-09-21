package io.github.danilotomassoni.core_hub.user_service.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.danilotomassoni.core_hub.user_service.entity.User;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);
    

    boolean existsByEmail(String email);
}
