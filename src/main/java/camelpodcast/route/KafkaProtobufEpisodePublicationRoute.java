package camelpodcast.route;

import camelpodcast.config.ApplicationConstants;
import camelpodcast.processor.PodcastPublicationProcessor;
import camelpodcast.proto.EpisodePublicationEvent;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dataformat.protobuf.ProtobufDataFormat;
import org.apache.camel.spi.IdempotentRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import camelpodcast.config.CryptoConfig;
import org.apache.camel.converter.crypto.CryptoDataFormat;

/**
 * Kafka flow that uses <strong>Google Protocol Buffers</strong> as the wire
 * format and an <em>Idempotent Consumer</em> EIP to discard duplicate episode
 * publication events - the pattern shown on Slide 16 of the source deck.
 *
 * <p>The {@code EpisodePublicationEvent} class is generated from
 * {@code src/main/proto/episode.proto} by {@code protobuf-maven-plugin}.</p>
 */
@Component
public class KafkaProtobufEpisodePublicationRoute extends RouteBuilder {

    public static final String PRODUCER_ROUTE_ID =
            "kafka-episode-publications-producer";

    public static final String CONSUMER_ROUTE_ID =
            "kafka-episode-publications-consumer";

    private final PodcastPublicationProcessor podcastPublicationProcessor;
    private final IdempotentRepository idempotentRepository;
    private final MeterRegistry meterRegistry;
    private final CryptoDataFormat aesFormat;

    public KafkaProtobufEpisodePublicationRoute(
            final PodcastPublicationProcessor episodePublicationProcessor,
            @Qualifier(ApplicationConstants.CAFFEINE_IDEMPOTENT_REPO)
            final IdempotentRepository idempotentRepository,
            final MeterRegistry meterRegistry,
            @Qualifier(CryptoConfig.AES_FORMAT)
            final CryptoDataFormat aesFormat) {
        this.podcastPublicationProcessor = episodePublicationProcessor;
        this.idempotentRepository = idempotentRepository;
        this.meterRegistry = meterRegistry;
        this.aesFormat = aesFormat;
    }

    @Override
    public void configure() {

        errorHandler(RouteSupport.defaultErrorHandler());

        // Default-instance-based ProtobufDataFormat for parsing the binary
        // payload back into a typed EpisodePublicationEvent object on the consumer side.
        final ProtobufDataFormat protobuf =
                new ProtobufDataFormat(EpisodePublicationEvent.getDefaultInstance());

        /* ---------------- Producer ---------------- */
        from(ApplicationConstants.DIRECT_PUBLISH_EPISODE_PUBLICATION)
                .routeId(PRODUCER_ROUTE_ID)
                .log(LoggingLevel.INFO,
                        "Publishing episode publication event (Protobuf) to Kafka topic ["
                                + ApplicationConstants
                                .KAFKA_TOPIC_EPISODE_PUBLICATION_PROTO + "]")
                .marshal(protobuf)
                .convertBodyTo(byte[].class)
                .log(org.apache.camel.LoggingLevel.INFO,
                        "After Protobuf marshal: type=${body.getClass().simpleName}")
                .marshal(aesFormat)
                .log(LoggingLevel.DEBUG,
                        "Payload encrypted (${bodyAs(byte[]).length} bytes ciphertext)")
                .toF("%s%s", ApplicationConstants.KAFKA_BASE_URI,
                        ApplicationConstants.KAFKA_TOPIC_EPISODE_PUBLICATION_PROTO);

        /* ---------------- Consumer with idempotent filter ---------------- */
        from(ApplicationConstants.KAFKA_BASE_URI
                + ApplicationConstants.KAFKA_TOPIC_EPISODE_PUBLICATION_PROTO
                + "?groupId=" + ApplicationConstants.KAFKA_GROUP_EPISODE_PUBLICATION)
                .routeId(CONSUMER_ROUTE_ID)
                .unmarshal(aesFormat)
                .unmarshal(protobuf)
                .setHeader(ApplicationConstants.HEADER_PUBLICATION_ID,
                        simple("${body.publicationId}"))
                .log(LoggingLevel.INFO,
                        ApplicationConstants.LOG_EPISODE_PUBLICATION_RECEIVED)
                // skipDuplicate(false) keeps duplicates flowing so we can count them.
                .idempotentConsumer(
                        header(ApplicationConstants.HEADER_PUBLICATION_ID),
                        idempotentRepository)
                .skipDuplicate(false)
                .choice()
                .when(exchangeProperty("CamelDuplicateMessage").isEqualTo(true))
                .log(LoggingLevel.WARN,
                        "Duplicate episode publication dropped: ${header."
                                + ApplicationConstants
                                .HEADER_PUBLICATION_ID + "}")
                .process(exchange -> Counter.builder(
                                ApplicationConstants.METRIC_DUPLICATES_BLOCKED)
                        .register(meterRegistry).increment())
                .stop()
                .otherwise()
                .bean(podcastPublicationProcessor, "process")
                .process(exchange -> Counter.builder(
                                ApplicationConstants
                                        .METRIC_EPISODE_PUBLICATIONS_PROCESSED)
                        .tag(ApplicationConstants.TAG_STATUS, "success")
                        .tag(ApplicationConstants.TAG_PLATFORM,
                                exchange.getMessage()
                                        .getBody(EpisodePublicationEvent.class)
                                        .getPlatform())
                        .register(meterRegistry)
                        .increment())
                .end();
    }
}