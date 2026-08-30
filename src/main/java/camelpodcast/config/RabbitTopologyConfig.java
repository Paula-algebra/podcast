package camelpodcast.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Declares the RabbitMQ Dead Letter Exchange (DLX) and Dead Letter Queue (DLQ)
 * topology used by the podcast flow. The main exchange/queue/binding are
 * declared by Camel's spring-rabbitmq component (via the autoDeclare flag and
 * the arg.* options on the consumer URI).
 */
@Configuration
public class RabbitTopologyConfig {

    @Bean
    public DirectExchange podcastDeadLetterExchange() {
        return new DirectExchange(ApplicationConstants.RABBIT_EXCHANGE_DLX,
                /* durable    */ true,
                /* autoDelete */ false);
    }

    @Bean
    public Queue podcastDeadLetterQueue() {
        return QueueBuilder
                .durable(ApplicationConstants.RABBIT_QUEUE_DLQ)
                .build();
    }

    @Bean
    public Binding podcastDeadLetterBinding() {
        return BindingBuilder.bind(podcastDeadLetterQueue())
                .to(podcastDeadLetterExchange())
                .with(ApplicationConstants.RABBIT_ROUTING_DLQ);
    }
}