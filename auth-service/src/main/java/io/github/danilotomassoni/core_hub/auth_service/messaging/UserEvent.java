package io.github.danilotomassoni.core_hub.auth_service.messaging;

import java.util.UUID;

public record UserEvent(
    UUID id,
    String username,
    String email,
    String role
) {

}
