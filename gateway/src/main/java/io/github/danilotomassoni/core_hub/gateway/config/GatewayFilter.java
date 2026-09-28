package io.github.danilotomassoni.core_hub.gateway.config;

import java.util.List;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import com.auth0.jwt.exceptions.JWTVerificationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Component
@RequiredArgsConstructor
public class GatewayFilter implements WebFilter {

    private final List<String> publicPaths = List.of("/auth/**", "/actuator/**");
    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    private final JWTService jwtService;

    // 1. Definição do Role-Based Access Control (RBAC)
    // Mapeia quais caminhos exigem quais privilégios.
    private final Map<String, List<String>> roleProtectedPaths = Map.of(
            "/users/**", List.of("ADMIN","USER"),
            "/products/**", List.of("ADMIN", "USER")
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();

        // 1. Handle Public Paths
        boolean isPublic = publicPaths.stream().anyMatch(pattern -> pathMatcher.match(pattern, path));
        if (isPublic) {
            return chain.filter(exchange)
                    .doFinally(signalType -> logAudit(exchange, "ANONYMOUS", "NONE"));
        }

        // 2. Validate Authorization Header Missing/Invalid Format
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete()
                    .doFinally(signalType -> logAudit(exchange, "MISSING_TOKEN", "NONE"));
        }

        String token = authHeader.substring(7);

        // 3. Process Token Validation, Role Enforcement & Route the Request
        return Mono.fromCallable(() -> jwtService.validateToken(token))
                .flatMap(decodedJwt -> {
                    String subject = decodedJwt.getSubject();
                    String role = decodedJwt.getClaim("role").asString();

                    // VALIDAÇÃO DA ROLE: Se o caminho for protegido e o usuário não tiver a role correta, barra com 403
                    if (!hasRolePermission(path, role)) {
                        exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN); // 403 Forbidden
                        return exchange.getResponse().setComplete()
                                .doFinally(signalType -> logAudit(exchange, subject, role + "_FORBIDDEN"));
                    }

                    ServerHttpRequest modifiedRequest = request.mutate()
                            .header("X-User-Sub", subject)
                            .header("X-User-Role", role)
                            .build();

                    return chain.filter(exchange.mutate().request(modifiedRequest).build())
                            .doFinally(signalType -> logAudit(exchange, subject, role));
                })
                .onErrorResume(JWTVerificationException.class, e -> {
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    return exchange.getResponse().setComplete()
                            .doFinally(signalType -> logAudit(exchange, "INVALID_TOKEN", "NONE"));
                });
    }

    /**
     * Helper method to verify if the user's role is allowed for the specific path.
     */
    private boolean hasRolePermission(String path, String userRole) {
        return roleProtectedPaths.entrySet().stream()
                .filter(entry -> pathMatcher.match(entry.getKey(), path))
                .map(entry -> entry.getValue())
                .findFirst()
                .map(allowedRoles -> allowedRoles.contains(userRole))
                .orElse(true);
    }

    /**
     * Asynchronously generates structured logs for auditing purposes.
     */
    private void logAudit(ServerWebExchange exchange, String actor, String role) {
        Mono.fromRunnable(() -> {
            ServerHttpRequest request = exchange.getRequest();
            String clientIp = request.getRemoteAddress() != null ? request.getRemoteAddress().toString() : "UNKNOWN";
           
            log.info("Actor {}, Role {}, IP {}", actor, role, clientIp);
        })
        .subscribeOn(Schedulers.boundedElastic())
        .subscribe();
    }
}
