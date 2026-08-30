package camelpodcast.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.UUID;

public record PodcastRequestEvent(
        @JsonProperty("requestId") String requestId,
        @JsonProperty("httpMethod") String httpMethod,
        @JsonProperty("endpoint") String endpoint,
        @JsonProperty("requestBody") String requestBody,
        @JsonProperty("createdAt") Instant createdAt) {

    @JsonCreator
    public PodcastRequestEvent {
        if (requestId == null || requestId.isBlank()) {
            throw new IllegalArgumentException("requestId must not be blank");
        }
        if (httpMethod == null || httpMethod.isBlank()) {
            throw new IllegalArgumentException("httpMethod must not be blank");
        }
        if (endpoint == null || endpoint.isBlank()) {
            throw new IllegalArgumentException("endpoint must not be blank");
        }
    }

    public static PodcastRequestEvent newRequest(
            String httpMethod,
            String endpoint,
            String requestBody) {

        return new PodcastRequestEvent(
                UUID.randomUUID().toString(),
                httpMethod,
                endpoint,
                requestBody,
                Instant.now());
    }
}