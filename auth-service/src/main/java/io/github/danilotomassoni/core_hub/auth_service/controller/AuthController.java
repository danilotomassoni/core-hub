package io.github.danilotomassoni.core_hub.auth_service.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.danilotomassoni.core_hub.auth_service.dto.request.LoginRequest;
import io.github.danilotomassoni.core_hub.auth_service.dto.request.RefreshTokenRequest;
import io.github.danilotomassoni.core_hub.auth_service.dto.request.RegisterRequest;
import io.github.danilotomassoni.core_hub.auth_service.dto.response.AuthResponse;
import io.github.danilotomassoni.core_hub.auth_service.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService service;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody @Valid RegisterRequest request) {
        service.register(request);
        return ResponseEntity.created(null).build();
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginRequest request) {
        return ResponseEntity.ok(service.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(service.refresh(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<AuthResponse> logout(@RequestBody RefreshTokenRequest request) {
        service.logout(request);
        return ResponseEntity.noContent().build();
    }

}
