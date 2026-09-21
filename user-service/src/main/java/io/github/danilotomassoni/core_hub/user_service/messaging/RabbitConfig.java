package io.github.danilotomassoni.core_hub.user_service.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    // --- Domínio: Auth ---
    public static final String EXCHANGE_NAME = "auth-service-exchange";
    public static final String QUEUE_NAME = "auth-service-queue";
    public static final String ROUTING_KEY = "auth-service-routing-key";

    // --- Domínio: User Update ---
    public static final String EXCHANGE_USER_UPDATE = "user.update.exchange";
    public static final String QUEUE_USER_UPDATE = "queue.user.update";
    public static final String ROUTING_KEY_ROLE_UPDATED = "user.role.updated";

    // --- Infra: DLX Global ou por Contexto ---
    public static final String DLX_EXCHANGE_NAME = "user-service-dlx-exchange";
    public static final String DLX_AUTH_QUEUE = "auth-service-dlq";
    public static final String DLX_AUTH_RK = "auth-service-dlq-rk";
    public static final String DLX_USER_QUEUE = "user-update-dlq";
    public static final String DLX_USER_RK = "user-update-dlq-rk";

    @Bean
    public TopicExchange authServiceExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public TopicExchange userUpdateExchange() {
        return new TopicExchange(EXCHANGE_USER_UPDATE);
    }

    @Bean
    public TopicExchange serviceDLXExchange() {
        return new TopicExchange(DLX_EXCHANGE_NAME);
    }

    // Filas Principais apontando para suas respectivas chaves na DLX
    @Bean
    public Queue authServiceQueue() {
        return QueueBuilder.durable(QUEUE_NAME)
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE_NAME)
                .withArgument("x-dead-letter-routing-key", DLX_AUTH_RK)
                .build();
    }

    @Bean
    public Queue userUpdateQueue() {
        return QueueBuilder.durable(QUEUE_USER_UPDATE)
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE_NAME)
                .withArgument("x-dead-letter-routing-key", DLX_USER_RK)
                .build();
    }

    // Definição das DLQs separadas
    @Bean
    public Queue authServiceDLQ() {
        return QueueBuilder.durable(DLX_AUTH_QUEUE).build();
    }

    @Bean
    public Queue userUpdateDLQ() {
        return QueueBuilder.durable(DLX_USER_QUEUE).build();
    }

    // Bindings Principais
    @Bean
    public Binding authServiceBinding() {
        return BindingBuilder.bind(authServiceQueue()).to(authServiceExchange()).with(ROUTING_KEY);
    }

    @Bean
    public Binding userUpdateBinding() {
        return BindingBuilder.bind(userUpdateQueue()).to(userUpdateExchange()).with(ROUTING_KEY_ROLE_UPDATED);
    }

    // Bindings das DLQs
    @Bean
    public Binding authDLQBinding() {
        return BindingBuilder.bind(authServiceDLQ()).to(serviceDLXExchange()).with(DLX_AUTH_RK);
    }

    @Bean
    public Binding userDLQBinding() {
        return BindingBuilder.bind(userUpdateDLQ()).to(serviceDLXExchange()).with(DLX_USER_RK);
    }

    @Bean
    public MessageConverter jacksonJsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jacksonJsonMessageConverter());
        return factory;
    }
}
