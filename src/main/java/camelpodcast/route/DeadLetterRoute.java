package camelpodcast.route;

import camelpodcast.config.ApplicationConstants;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;
import camelpodcast.config.CryptoConfig;
import org.apache.camel.converter.crypto.CryptoDataFormat;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Defines the in-process Dead Letter pipe used by every other route's
 * {@link RouteSupport#defaultErrorHandler() default error handler}. After
 * exponential-backoff retries are exhausted, the failed exchange lands here
 * and is forwarded to the Kafka DLQ topic, tagged with the originating route.
 *
 * <p>This implements the Dead Letter Channel pattern shown on Slide 16 of
 * the source presentation.</p>
 */
@Component
public class DeadLetterRoute extends RouteBuilder {

    /** Camel id of the DLQ-pipe route. */
    public static final String DLQ_ROUTE_ID = "global-dead-letter-pipe";
    private final CryptoDataFormat aesFormat;

    public DeadLetterRoute(
            @Qualifier(CryptoConfig.AES_FORMAT)
            final CryptoDataFormat aesFormat) {
        this.aesFormat = aesFormat;
    }

    @Override
    public void configure() {
        from(RouteSupport.DLQ_ENDPOINT_URI)
                .routeId(DLQ_ROUTE_ID)
                .log(LoggingLevel.ERROR,
                        "Sending failed exchange to DLQ topic: "
                                + "cause=${exception.message}, body=${body}")
                .setHeader(ApplicationConstants.HEADER_SOURCE,
                        simple("${routeId}"))
                .convertBodyTo(byte[].class)
                .marshal(aesFormat)
                .toF("%s%s", ApplicationConstants.KAFKA_BASE_URI,
                        ApplicationConstants.KAFKA_TOPIC_DLQ);
    }
}