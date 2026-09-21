package io.github.danilotomassoni.core_hub.auth_service.service;

import io.github.danilotomassoni.core_hub.auth_service.dto.request.LoginRequest;
import io.github.danilotomassoni.core_hub.auth_service.dto.request.RefreshTokenRequest;
import io.github.danilotomassoni.core_hub.auth_service.dto.request.RegisterRequest;
import io.github.danilotomassoni.core_hub.auth_service.dto.response.AuthResponse;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse refresh(RefreshTokenRequest request);

    void logout(RefreshTokenRequest request);
}
