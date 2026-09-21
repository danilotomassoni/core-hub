package io.github.danilotomassoni.core_hub.auth_service.service.impl;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import io.github.danilotomassoni.core_hub.auth_service.dto.request.LoginRequest;
import io.github.danilotomassoni.core_hub.auth_service.dto.request.RefreshTokenRequest;
import io.github.danilotomassoni.core_hub.auth_service.dto.request.RegisterRequest;
import io.github.danilotomassoni.core_hub.auth_service.dto.response.AuthResponse;
import io.github.danilotomassoni.core_hub.auth_service.entity.RefreshToken;
import io.github.danilotomassoni.core_hub.auth_service.entity.RoleType;
import io.github.danilotomassoni.core_hub.auth_service.entity.User;
import io.github.danilotomassoni.core_hub.auth_service.exception.EmailAlreadyExistsException;
import io.github.danilotomassoni.core_hub.auth_service.mapper.UserMapper;
import io.github.danilotomassoni.core_hub.auth_service.messaging.RabbitProducer;
import io.github.danilotomassoni.core_hub.auth_service.messaging.UserEvent;
import io.github.danilotomassoni.core_hub.auth_service.repository.UserRepository;
import io.github.danilotomassoni.core_hub.auth_service.service.AuthService;
import io.github.danilotomassoni.core_hub.auth_service.service.JWTService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository repository;
    private final UserMapper mapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager manager;
    private final JWTService jwtService;
    private final RefreshTokenServiceImpl refreshTokenService;
    private final RabbitProducer rabbitProducer;

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (repository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException("Email already exists.");
        }
        User user = mapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(RoleType.USER);
        user.setActive(true);
        User saved = repository.save(user);

        String access = jwtService.access(saved);
        RefreshToken refresh = refreshTokenService.createRefreshToken(user);
        
        rabbitProducer.sendMessage(new UserEvent(saved.getId(), saved.getUsername(), saved.getEmail(), saved.getRole().name()));
        
        log.info("User registered and authenticated successfully. ID: {}", saved.getId());
        return new AuthResponse(access, refresh.getToken());
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        User user = repository.findByEmail(request.email())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password."));

        Authentication authentication = new UsernamePasswordAuthenticationToken(request.email(), request.password());
        manager.authenticate(authentication);

        String access = jwtService.access(user);
        RefreshToken refresh = refreshTokenService.createRefreshToken(user);
        
        log.info("User authenticated successfully via login. ID: {}", user.getId());
        return new AuthResponse(access, refresh.getToken());
    }

    @Override
    public AuthResponse refresh(RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenService.findByToken(request.token())
                .orElseThrow(() -> new BadCredentialsException("Invalid refresh token."));

        User user = refreshToken.getUser();

        String access = jwtService.access(user);
        RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user);
        
        log.info("Token refreshed successfully for user. ID: {}", user.getId());
        return new AuthResponse(access, newRefreshToken.getToken());
    }

    @Override
    public void logout(RefreshTokenRequest request) {
        refreshTokenService.deleteToken(request.token());
        log.info("User logged out successfully. Token invalidated.");
    }
}
