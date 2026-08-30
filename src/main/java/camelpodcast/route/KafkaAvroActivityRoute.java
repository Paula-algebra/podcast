package camelpodcast.route;

import camelpodcast.avro.EpisodeActivity;
import camelpodcast.config.ApplicationConstants;
import camelpodcast.processor.EpisodeActivityProcessor;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dataformat.avro.AvroDataFormat;
import org.springframework.stereotype.Component;
import camelpodcast.config.CryptoConfig;
import org.apache.camel.converter.crypto.CryptoDataFormat;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Kafka producer/consumer flow that uses <strong>Apache Avro</strong> as the
 * binary wire format - mirroring the IoT-sensors pipeline from Slide 6 of the
 * source deck.
 *
 * <p>The {@link EpisodeActivity} class is generated from
 * {@code src/main/resources/avro/episode-activity.avsc} by the
 * {@code avro-maven-plugin}.</p>
 */
@Component
public class KafkaAvroActivityRoute extends RouteBuilder {

    public static final String PRODUCER_ROUTE_ID =
            "kafka-episode-activities-producer";

    public static final String CONSUMER_ROUTE_ID =
            "kafka-episode-activities-consumer";

    private final EpisodeActivityProcessor episodeActivityProcessor;
    private final MeterRegistry meterRegistry;
    private final CryptoDataFormat aesFormat;

    public KafkaAvroActivityRoute(
            final EpisodeActivityProcessor episodeActivityProcessor,
            final MeterRegistry meterRegistry,
            @Qualifier(CryptoConfig.AES_FORMAT)
            final CryptoDataFormat aesFormat) {
        this.episodeActivityProcessor = episodeActivityProcessor;
        this.meterRegistry = meterRegistry;
        this.aesFormat = aesFormat;
    }

    @Override
    public void configure() {

        errorHandler(RouteSupport.defaultErrorHandler());

        // Use the Avro schema generated from episode-activity.avsc.
        final AvroDataFormat avroFormat =
                new AvroDataFormat(EpisodeActivity.getClassSchema());

        /* ---------------- Producer ---------------- */
        from(ApplicationConstants.DIRECT_PUBLISH_EPISODE_ACTIVITY)
                .routeId(PRODUCER_ROUTE_ID)
                .log(LoggingLevel.INFO,
                        "Publishing episode activity to Kafka topic ["
                                + ApplicationConstants
                                .KAFKA_TOPIC_EPISODE_ACTIVITY_AVRO + "]")
                .marshal(avroFormat)
                .convertBodyTo(byte[].class)
                .log(org.apache.camel.LoggingLevel.INFO,
                        "After Avro marshal: type=${body.getClass().simpleName}")
                .marshal(aesFormat)
                .log(LoggingLevel.DEBUG,
                        "Payload encrypted (${bodyAs(byte[]).length} bytes ciphertext)")
                .toF("%s%s", ApplicationConstants.KAFKA_BASE_URI,
                        ApplicationConstants.KAFKA_TOPIC_EPISODE_ACTIVITY_AVRO);

        /* ---------------- Consumer ---------------- */
        // Note: groupId, isolation level and auto offset reset are configured
        // globally via application.yml (camel.component.kafka.*); per-route
        // overrides are appended here for clarity.
        from(ApplicationConstants.KAFKA_BASE_URI
                + ApplicationConstants.KAFKA_TOPIC_EPISODE_ACTIVITY_AVRO
                + "?groupId=" + ApplicationConstants.KAFKA_GROUP_EPISODE_ANALYTICS)
                .routeId(CONSUMER_ROUTE_ID)
                .unmarshal(aesFormat)
                .unmarshal(avroFormat)
                .setHeader(ApplicationConstants.HEADER_ACTIVITY_ID,
                        simple("${body.eventId}"))
                .log(LoggingLevel.INFO,
                        ApplicationConstants.LOG_EPISODE_ACTIVITY_RECEIVED)
                .bean(episodeActivityProcessor, "process")
                .process(exchange -> Counter.builder(
                                ApplicationConstants.METRIC_EPISODE_ACTIVITIES_PROCESSED)
                        .tag(ApplicationConstants.TAG_STATUS, "success")
                        .register(meterRegistry)
                        .increment());
    }
}