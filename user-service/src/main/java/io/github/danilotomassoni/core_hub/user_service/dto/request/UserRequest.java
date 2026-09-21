package io.github.danilotomassoni.core_hub.user_service.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserRequest(
    @NotBlank String username,
    @NotBlank @Email  String email
) {}
