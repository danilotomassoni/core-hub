package io.github.danilotomassoni.core_hub.user_service.messaging;

import java.util.UUID;

import io.github.danilotomassoni.core_hub.user_service.entity.RoleType;

public record UserRoleEvent(
    UUID id,
    RoleType role
) {

}
