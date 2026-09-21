package io.github.danilotomassoni.core_hub.user_service.dto.response;

import java.util.UUID;

import io.github.danilotomassoni.core_hub.user_service.entity.RoleType;

public record UserResponse(
    UUID id,
    String username,
    String email,
    RoleType role
) {

}
