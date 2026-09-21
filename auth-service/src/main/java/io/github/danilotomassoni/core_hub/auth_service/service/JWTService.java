package io.github.danilotomassoni.core_hub.auth_service.service;

import com.auth0.jwt.interfaces.DecodedJWT;

import io.github.danilotomassoni.core_hub.auth_service.entity.User;

public interface JWTService {

    String access(User user);
    String refresh(User user);
    DecodedJWT decode(String token);
    boolean validation(String token);
}
