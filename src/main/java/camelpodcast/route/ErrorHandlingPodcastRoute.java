package camelpodcast.route;

import camelpodcast.config.ApplicationConstants;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * Demonstrates two error-handling patterns from Slide 16 of the deck:
 *
 * <ol>
 *   <li><strong>Dead Letter Channel</strong> - inherited from the global
 *       error handler defined in {@link GlobalErrorHandlerRoute}; this route
 *       deliberately throws to trigger redelivery + DLQ.</li>
 *   <li><strong>Circuit Breaker (Resilience4j)</strong> - protects a flaky
 *       downstream endpoint and serves a fallback response when the breaker
 *       is open.</li>
 * </ol>
 */
@Component
public class ErrorHandlingPodcastRoute extends RouteBuilder {

    public static final String FAULTY_ROUTE_ID = "demo-faulty-route";
    public static final String CIRCUIT_BREAKER_ROUTE_ID = "demo-circuit-breaker-route";

    @Override
    public void configure() {

        errorHandler(RouteSupport.defaultErrorHandler());

        /* ----- 1. Faulty endpoint - exercises Dead Letter Channel ----- */
        from(ApplicationConstants.DIRECT_FAULTY)
                .routeId(FAULTY_ROUTE_ID)
                .log(LoggingLevel.INFO, "Entering faulty route - this WILL fail")
                .throwException(new IllegalStateException(
                        "Simulated downstream failure"));

        /* ----- 2. Circuit-breaker-protected endpoint ----- */
        from(ApplicationConstants.DIRECT_CB_PROTECTED)
                .routeId(CIRCUIT_BREAKER_ROUTE_ID)
                .circuitBreaker()
                .resilience4jConfiguration()
                .slidingWindowSize(10)
                .failureRateThreshold(50.0F)
                .waitDurationInOpenState(10_000)
                .permittedNumberOfCallsInHalfOpenState(3)
                .minimumNumberOfCalls(5)
                .end()
                .log(LoggingLevel.INFO,
                        "Calling protected operation, body=${body}")
                .process(exchange -> {
                    final String body = exchange.getMessage().getBody(String.class);
                    if (body != null && body.contains("FAIL")) {
                        throw new RuntimeException(
                                "Forced failure to open the circuit");
                    }
                    exchange.getMessage().setBody("OK - " + body);
                })
                .onFallback()
                .log(LoggingLevel.WARN,
                        "Circuit breaker fallback engaged for body=${body}")
                .setBody(constant("FALLBACK"))
                .end();
    }
}