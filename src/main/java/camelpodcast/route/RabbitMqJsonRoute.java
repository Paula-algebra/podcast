package camelpodcast.route;

import camelpodcast.config.ApplicationConstants;
import camelpodcast.model.PodcastRequestEvent;
import camelpodcast.processor.EpisodeRequestProcessor;
import camelpodcast.processor.ResponseProcessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.springframework.stereotype.Component;

/** Module 6 */
import camelpodcast.config.CryptoConfig;
import org.apache.camel.converter.crypto.CryptoDataFormat;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * RabbitMQ + JSON flow.
 *
 * <ul>
 *   <li><b>Producer:</b> {@code direct:publishEpisodeRequest} -&gt; marshal to JSON
 *       -&gt; publish to topic exchange {@code podcast.exchange} with routing
 *       key {@code podcast.request}.</li>
 *   <li><b>Consumer:</b> bind {@code podcast.requests.queue} to that exchange,
 *       unmarshal JSON to {@link PodcastRequestEvent}, run business processor,
 *       forward the request to the Podcast REST API, count successes via Micrometer.
 *       Failed messages are routed to a Dead Letter Exchange ({@code podcast.dlx})
 *       so the broker itself can hold the failed messages.</li>
 * </ul>
 *
 * <p>The "FinTech / fan-out" pattern from Slide 5 of the deck is illustrated
 * by the topic exchange + routing key + DLX combo.</p>
 */
@Component
public class RabbitMqJsonRoute extends RouteBuilder {

    /** Camel route id of the producer side. */
    public static final String PRODUCER_ROUTE_ID = "rabbit-podcast-producer";
    /** Camel route id of the consumer side. */
    public static final String CONSUMER_ROUTE_ID = "rabbit-podcast-consumer";

    private final EpisodeRequestProcessor episodeRequestProcessor;
    private final ResponseProcessor responseProcessor;
    private final MeterRegistry meterRegistry;
    /** Module 6 */
    private final CryptoDataFormat aesFormat;

    public RabbitMqJsonRoute(
            final EpisodeRequestProcessor episodeRequestProcessor,
            final ResponseProcessor responseProcessor,
            final MeterRegistry meterRegistry,
            /** Module 6 */
            @Qualifier(CryptoConfig.AES_FORMAT)
            final CryptoDataFormat aesFormat
            ) {
        this.episodeRequestProcessor = episodeRequestProcessor;
        this.responseProcessor = responseProcessor;
        this.meterRegistry = meterRegistry;
        /** Module 6 */
        this.aesFormat = aesFormat;
    }

    @Override
    public void configure() {

        errorHandler(RouteSupport.defaultErrorHandler());

        // Build a Jackson data format that knows how to (de)serialize
        // Java 8 date/time types like Instant and LocalDateTime.
        final ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(com.fasterxml.jackson.databind.SerializationFeature
                        .WRITE_DATES_AS_TIMESTAMPS);

        final JacksonDataFormat jsonFormat =
                new JacksonDataFormat(PodcastRequestEvent.class);
        jsonFormat.setObjectMapper(objectMapper);

        /* ---------------- Producer ---------------- */
        from(ApplicationConstants.DIRECT_PUBLISH_EPISODE_REQUEST)
                .routeId(PRODUCER_ROUTE_ID)
                .log(LoggingLevel.INFO,
                        "Publishing episode request to RabbitMQ exchange ["
                                + ApplicationConstants.RABBIT_EXCHANGE_PODCAST + "]")
                .marshal(jsonFormat)
                /** Module 6 */
                .convertBodyTo(byte[].class)
                .log(org.apache.camel.LoggingLevel.INFO,
                        "After JSON marshal: type=${body.getClass().simpleName}")
                .marshal(aesFormat)
                .log(LoggingLevel.DEBUG,
                        "Payload encrypted (${bodyAs(byte[]).length} bytes ciphertext)")

                .to(ApplicationConstants.SPRING_RABBITMQ_PRODUCER_URI);

        /* ---------------- Consumer ---------------- */
        from(ApplicationConstants.SPRING_RABBITMQ_CONSUMER_URI)
                .routeId(CONSUMER_ROUTE_ID)
                /** Module 6 */
                .unmarshal(aesFormat)

                .unmarshal(jsonFormat)
                .setHeader(ApplicationConstants.HEADER_REQUEST_ID,
                        simple("${body.requestId}"))
                .log(LoggingLevel.INFO,
                        ApplicationConstants.LOG_EPISODE_REQUEST_RECEIVED)
                .bean(episodeRequestProcessor, "process")
                .toD("${exchangeProperty.endpoint}?throwExceptionOnFailure=true")
                .bean(responseProcessor, "process")
                .process(exchange -> Counter.builder(
                                ApplicationConstants.METRIC_EPISODES_PROCESSED)
                        .tag(ApplicationConstants.TAG_STATUS, "success")
                        .register(meterRegistry)
                        .increment());
    }
}