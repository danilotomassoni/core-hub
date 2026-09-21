package io.github.danilotomassoni.core_hub.auth_service.service;

import java.util.Optional;
import io.github.danilotomassoni.core_hub.auth_service.entity.RefreshToken;
import io.github.danilotomassoni.core_hub.auth_service.entity.User;

public interface RefreshTokenService {

    // Busca um token de refresh pelo valor em string
    Optional<RefreshToken> findByToken(String token);

    // Cria ou atualiza o refresh token associado a um usuário
    RefreshToken createRefreshToken(User user);

    void deleteToken(String token);

    
}
