package camelpodcast.rest;

import camelpodcast.config.ApplicationConstants;
import camelpodcast.service.EventGeneratorService;
import org.apache.camel.ProducerTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Thin HTTP layer that lets the operator trigger each route from a browser
 * or {@code curl}. Useful for live demos and for the README's smoke-test
 * recipes.
 */
@RestController
@RequestMapping("/api/demo")
public class PodcastController {

    private final EventGeneratorService generator;
    private final ProducerTemplate producerTemplate;

    public PodcastController(final EventGeneratorService generator,
                          final ProducerTemplate producerTemplate) {
        this.generator = generator;
        this.producerTemplate = producerTemplate;
    }

    /* ---------- Producers ---------- */

    @PostMapping("/episode")
    public ResponseEntity<Map<String, Object>> publishEpisode() {
        final var request = generator.sendRandomEpisodeRequest();
        return ResponseEntity.ok(Map.of(
                "message", "Episode request published to RabbitMQ (JSON)",
                "request", request));
    }

    @PostMapping("/activity")
    public ResponseEntity<Map<String, Object>> publishActivity() {
        final var activity = generator.sendRandomEpisodeActivity();
        return ResponseEntity.ok(Map.of(
                "message", "Episode activity published to Kafka (Avro)",
                "eventId", activity.getEventId(),
                "episodeId", activity.getEpisodeId(),
                "operation", activity.getOperation()));
    }

    @PostMapping("/publication")
    public ResponseEntity<Map<String, Object>> publishPublication() {
        final var publication = generator.sendRandomEpisodePublication();
        return ResponseEntity.ok(Map.of(
                "message", "Episode publication published to Kafka (Protobuf)",
                "publicationId", publication.getPublicationId(),
                "episodeId", publication.getEpisodeId(),
                "platform", publication.getPlatform()));
    }

    @PostMapping("/publication/duplicate")
    public ResponseEntity<Map<String, Object>> publishDuplicatePublication() {
        final var publication = generator.sendDuplicateEpisodePublication();
        return ResponseEntity.ok(Map.of(
                "message", "Same episode publication published twice - duplicate must be filtered",
                "publicationId", publication.getPublicationId()));
    }

    /* ---------- Error handling demos ---------- */

    @PostMapping("/error/dlq")
    public ResponseEntity<Map<String, Object>> triggerDeadLetterChannel() {
        try {
            producerTemplate.sendBody(ApplicationConstants.DIRECT_FAULTY,
                    "trigger-dlq");
        } catch (final Exception ignored) {
            // The exchange has been routed to the DLQ by the global error handler.
        }
        return ResponseEntity.ok(Map.of(
                "message", "Faulty exchange triggered, see DLQ topic 'podcast-dlq'"));
    }

    @PostMapping("/error/circuit-breaker")
    public ResponseEntity<Map<String, Object>> circuitBreakerCall(
            @RequestBody final String body) {
        final String result = producerTemplate.requestBody(
                ApplicationConstants.DIRECT_CB_PROTECTED, body, String.class);
        return ResponseEntity.ok(Map.of("result", result));
    }

    /* ---------- Health helper ---------- */

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of("status", "UP"));
    }
}