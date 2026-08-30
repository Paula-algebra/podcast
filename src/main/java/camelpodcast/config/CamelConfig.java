package camelpodcast.config;

import io.micrometer.core.instrument.MeterRegistry;
import org.apache.camel.component.springrabbit.SpringRabbitMQComponent;
import org.apache.camel.spi.IdempotentRepository;
import org.apache.camel.spring.boot.CamelContextConfiguration;
import org.apache.camel.support.processor.idempotent.MemoryIdempotentRepository;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Camel-specific Spring configuration:
 * <ul>
 *   <li>An in-memory idempotent repository used by the Kafka episode-publication route</li>
 *   <li>A {@link CamelContextConfiguration} that ensures stream caching is enabled</li>
 * </ul>
 *
 * <p>Micrometer route policy and Prometheus exposition are auto-configured by
 * the <code>camel-micrometer-prometheus-starter</code> when the Spring Boot
 * Actuator <code>prometheus</code> endpoint is exposed.</p>
 */
@Configuration
public class CamelConfig {

    /**
     * Lightweight in-memory idempotent repository. For production use a
     * persistent backend instead (JDBC, Redis, Infinispan, Hazelcast, ...).
     *
     * @return a memory-backed {@link IdempotentRepository}
     */
    @Bean(name = ApplicationConstants.CAFFEINE_IDEMPOTENT_REPO)
    public IdempotentRepository episodePublicationIdempotentRepo() {
        return MemoryIdempotentRepository.memoryIdempotentRepository(2_000);
    }

    /**
     * Camel context customization hook - executed before the context starts.
     * We turn on stream caching so the same {@code Exchange} body can be read
     * by multiple downstream processors (e.g. logging + bean invocation).
     *
     * @param meterRegistry injected to make this configuration aware of the
     *                      Micrometer registry; the registry itself is used
     *                      transparently by the Camel auto-config.
     * @return a no-op {@link CamelContextConfiguration} that toggles streamCaching.
     */
    @Bean
    public CamelContextConfiguration camelContextConfiguration(
            final MeterRegistry meterRegistry) {
        return new CamelContextConfiguration() {
            @Override
            public void beforeApplicationStart(
                    final org.apache.camel.CamelContext context) {
                context.setStreamCaching(true);
            }

            @Override
            public void afterApplicationStart(
                    final org.apache.camel.CamelContext context) {
                // No-op: route policies for Micrometer are wired by auto-configuration.
            }
        };
    }

    /**
     * Wires the Spring AMQP {@link ConnectionFactory} (auto-configured by
     * Spring Boot from {@code spring.rabbitmq.*} properties) into Camel's
     * {@code spring-rabbitmq} component.
     *
     * <p>Camel normally auto-detects a {@code ConnectionFactory} from its
     * registry, but in Spring Boot 4 the bean ordering is sensitive enough
     * that explicit wiring is the safest approach. We also enable
     * {@code autoDeclare=true} so Camel cooperates with the queues and
     * exchanges declared as Spring AMQP beans in
     * {@link RabbitTopologyConfig}.</p>
     *
     * @param connectionFactory the Spring AMQP connection factory provided
     *                          by Spring Boot's RabbitMQ auto-configuration
     * @return the configured Camel {@code spring-rabbitmq} component
     */
    @Bean(name = "spring-rabbitmq")
    public SpringRabbitMQComponent springRabbitMQComponent(
            final ConnectionFactory connectionFactory) {
        final SpringRabbitMQComponent component = new SpringRabbitMQComponent();
        component.setConnectionFactory(connectionFactory);
        component.setAutoDeclare(true);
        return component;
    }
}