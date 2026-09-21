package io.github.danilotomassoni.core_hub.user_service.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service 
@RequiredArgsConstructor
public class RabbitProducer {

    private final RabbitTemplate rabbitTemplate;

    public void sendUserRoleUpdatedEvent(UserRoleEvent event) {
        log.info("Sending user role update event to auth service. ID: {}, New Role: {}", event.id(), event.role());
        
        try {
            rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE_USER_UPDATE, RabbitConfig.ROUTING_KEY_ROLE_UPDATED, event);
            log.info("User role update event sent successfully. ID: {}", event.id());
        } catch (Exception e) {
            log.error("Failed to send user role update event to RabbitMQ. ID: {}", event.id(), e);
            // Dependendo da criticidade, você pode lançar uma exceção para dar rollback na transação do banco
            throw new RuntimeException("Messaging failure when updating user role", e);
        }
    }
}
