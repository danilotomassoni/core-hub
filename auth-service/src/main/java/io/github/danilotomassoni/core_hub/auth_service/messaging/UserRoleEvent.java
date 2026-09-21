package io.github.danilotomassoni.core_hub.auth_service.messaging;

import java.util.UUID;

import io.github.danilotomassoni.core_hub.auth_service.entity.RoleType;

public record UserRoleEvent(
    UUID id,
    RoleType role
) {

}
