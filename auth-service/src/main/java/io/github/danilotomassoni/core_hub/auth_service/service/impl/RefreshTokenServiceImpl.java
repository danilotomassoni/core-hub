package io.github.danilotomassoni.core_hub.auth_service.service.impl;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import io.github.danilotomassoni.core_hub.auth_service.entity.RefreshToken;
import io.github.danilotomassoni.core_hub.auth_service.entity.User;
import io.github.danilotomassoni.core_hub.auth_service.repository.RefreshTokenRepository;
import io.github.danilotomassoni.core_hub.auth_service.service.JWTService;
import io.github.danilotomassoni.core_hub.auth_service.service.RefreshTokenService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j 
@Service
@RequiredArgsConstructor

public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository repository;
    private  final JWTService jwtService;

    @Transactional 
    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return  repository.findByToken(token);
    }

    @Transactional 
    @Override
    public RefreshToken createRefreshToken(User user) {

        repository.deleteByUser(user);

        RefreshToken refreshToken = new RefreshToken();
        String token = jwtService.refresh(user);

        refreshToken.setExpiration(Instant.now().plus(7, ChronoUnit.DAYS));
        refreshToken.setUser(user);
        refreshToken.setToken(token);

        repository.save(refreshToken);

        return  refreshToken;
    }

    @Transactional 
    @Override
    public void deleteToken(String token) {
        RefreshToken refreshToken = repository.findByToken(token).orElseThrow(()-> new BadCredentialsException("Token Inválido"));

        repository.deleteByUser(refreshToken.getUser());
    }
}
