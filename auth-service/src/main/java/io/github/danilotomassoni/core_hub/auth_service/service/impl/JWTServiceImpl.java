package io.github.danilotomassoni.core_hub.auth_service.service.impl;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import io.github.danilotomassoni.core_hub.auth_service.entity.User;
import io.github.danilotomassoni.core_hub.auth_service.service.JWTService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JWTServiceImpl implements JWTService {

    @Override
    public String access(User user) {
        return JWT.create()
                .withIssuer("auth-service")
                .withSubject(user.getId().toString())
                .withClaim("role", user.getRole().name())
                .withExpiresAt(Instant.now().plus(15, ChronoUnit.MINUTES))
                .sign(Algorithm.HMAC256("3HrO1B1WWrn8PFGNDU3WEaCp7B0NIug6obRy5uVkXOU="));
    }

    @Override
    public String refresh(User user) {
        return JWT.create()
                .withIssuer("auth-service")
                .withSubject(user.getId().toString())
                .withClaim("role", user.getRole().name())
                .withExpiresAt(Instant.now().plus(7, ChronoUnit.DAYS))
                .sign(Algorithm.HMAC256("4GKHOsjWLUH6O9mAkkCQnGQH4UMgYy1KnzIHynqkqQk="));

    }

    @Override
    public DecodedJWT decode(String token) {
        try{
            
            return JWT.decode(token);
        }catch(JWTDecodeException ex){
            return  null;
        }
    }

    @Override
    public boolean validation(String token) {
        try{
            JWT.require(Algorithm.HMAC256("4GKHOsjWLUH6O9mAkkCQnGQH4UMgYy1KnzIHynqkqQk=")).build().verify(token);
            return  true;
        }catch(JWTVerificationException ex){
            return  false;
        }
    }
}
