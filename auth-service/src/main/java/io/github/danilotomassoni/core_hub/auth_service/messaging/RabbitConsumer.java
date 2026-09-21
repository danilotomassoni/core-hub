package io.github.danilotomassoni.core_hub.auth_service.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import io.github.danilotomassoni.core_hub.auth_service.entity.RoleType;
import io.github.danilotomassoni.core_hub.auth_service.entity.User;
import io.github.danilotomassoni.core_hub.auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j 
@Component 
@RequiredArgsConstructor 
public class RabbitConsumer {

    private final UserRepository repository;

    @RabbitListener(queues = RabbitConfig.QUEUE_USER_UPDATE)
    public void receiveUserEvent(UserRoleEvent event) {
        log.info("Processing received event to save user: {}", event.id());

        // 1. Busca o usuário de forma performática (apenas uma consulta ao banco)
        User user = repository.findById(event.id()).orElse(null);
        if (user == null) {
            log.warn("User does not exist with ID: {}. Skipping message.", event.id());
            return; // Retorno silencioso (consome a mensagem) pois o usuário não existe no banco
        }

        // 2. Validação segura do Enum RoleType
        RoleType validatedRole;
        try {
            validatedRole = event.role();
        } catch (IllegalArgumentException | NullPointerException e) {
            log.error("Invalid role type '{}' received for user ID: {}. Skipping message.", event.role(), event.id());
            return; // Retorno silencioso se o payload veio corrompido/inválido de fábrica
        }

        // 3. Atualiza os dados
        user.setRole(validatedRole);

        try {
            repository.save(user);
            log.info("User updated successfully via RabbitMQ consumer. ID: {}", event.id());
        } catch (Exception e) {
            log.error("Failed to save user via RabbitMQ consumer. ID: {}", event.id(), e);
            // Lança a exceção original para que o mecanismo de Retry configurado no yml
            // tente reprocessar a mensagem antes de enviá-la definitivamente para a DLQ
            throw e; 
        }
    }
}
