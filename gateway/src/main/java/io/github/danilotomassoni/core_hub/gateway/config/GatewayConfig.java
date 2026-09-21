package io.github.danilotomassoni.core_hub.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.reactive.CorsConfigurationSource;


@Configuration
@EnableWebFluxSecurity
public class GatewayConfig {

    @Bean
    SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity https, CorsConfigurationSource corsConfigurationSource) {
        https
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource))
            .authorizeExchange(ex -> ex
                .pathMatchers("/actuator/**").permitAll()
                .pathMatchers("/auth/**").permitAll()
                .anyExchange().permitAll()
            );

        return https.build();
    }

}
