package io.github.danilotomassoni.core_hub.user_service.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import io.github.danilotomassoni.core_hub.user_service.dto.response.UserResponse;
import io.github.danilotomassoni.core_hub.user_service.entity.User;
import io.github.danilotomassoni.core_hub.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitConsumer {

    private final UserRepository repository;

    @RabbitListener(queues = RabbitConfig.QUEUE_NAME)
    public void receiveUserEvent(UserResponse event) {
        log.info("Processing received event to save user: {}", event.id());

        if (repository.existsByEmail(event.email())) {
            log.info("User already exists with email: {}", event.email());
            return;
        }

        User user = new User();
        user.setId(event.id());
        user.setUsername(event.username());
        user.setEmail(event.email());
        user.setRole(event.role());

        try {
            repository.save(user);
            log.info("User saved successfully via RabbitMQ consumer. Email: {}", event.email());
        } catch (Exception e) {
            log.error("Failed to save user via RabbitMQ consumer. Email: {}", event.email(), e);
            throw new org.springframework.amqp.AmqpRejectAndDontRequeueException("Failed to save user from event queue", e);
        }
    }
}
