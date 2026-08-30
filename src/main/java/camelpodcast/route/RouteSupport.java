package camelpodcast.route;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.DeadLetterChannelBuilder;
import org.apache.camel.builder.DefaultErrorHandlerBuilder;
import org.apache.camel.model.errorhandler.DefaultErrorHandlerDefinition;

/**
 * Static helper that returns a fully configured Dead Letter Channel error
 * handler. Routes that want production-grade retry + DLQ semantics simply
 * call {@link #defaultErrorHandler()} from their {@code configure()} method.
 *
 * <p>Centralising the configuration here keeps every route's redelivery
 * policy in lock-step and avoids hard-coded duplication.</p>
 */
public final class RouteSupport {

    /** URI of the in-process Camel route that ships failed exchanges to Kafka. */
    public static final String DLQ_ENDPOINT_URI = "direct:globalDlq";

    private RouteSupport() {
        // Utility class - no instances.
    }

    /**
     * Builds a {@link DeadLetterChannelBuilder} with the demo's standard policy:
     * up to 3 redeliveries, exponential back-off (1s -&gt; 30s cap), and the
     * original message preserved on dispatch to the DLQ.
     *
     * @return an error-handler definition ready to be passed to {@code errorHandler(...)}
     */
    public static DefaultErrorHandlerDefinition  defaultErrorHandler() {
        return new DeadLetterChannelBuilder(DLQ_ENDPOINT_URI)
                .maximumRedeliveries(3)
                .redeliveryDelay(1_000L)
                .useExponentialBackOff()
                .backOffMultiplier(2.0d)
                .maximumRedeliveryDelay(30_000L)
                .retryAttemptedLogLevel(LoggingLevel.WARN)
                .retriesExhaustedLogLevel(LoggingLevel.ERROR)
                .logExhaustedMessageHistory(true)
                .useOriginalMessage();
    }
}
