package io.github.danilotomassoni.core_hub.auth_service.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterRequest(
    @NotBlank(message = "Username is required") 
    String username,
    
    @NotBlank(message = "Email is required") 
    @Email(message = "Must be a well-formed email address") 
    String email,
    
    @NotBlank(message = "Password is required") 
    String password
) {}
