package io.github.danilotomassoni.core_hub.user_service.dto.request;

import io.github.danilotomassoni.core_hub.user_service.entity.RoleType;
import jakarta.validation.constraints.NotNull;

public record RoleRequest(
    @NotNull (message = "Role is required") RoleType role
) {

}
