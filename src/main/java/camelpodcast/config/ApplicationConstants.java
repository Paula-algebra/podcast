package camelpodcast.config;

/**
 * Centralized string constants used by Camel routes, processors and REST controllers.
 *
 * <p>Following the "no hard-coded strings in business logic" rule, every endpoint URI,
 * exchange / queue name, header name and metric name is declared here exactly once.</p>
 */
public final class ApplicationConstants {

    private ApplicationConstants() {
        // Utility class, no instances.
    }

    /* ===================== Direct (in-memory) endpoints ===================== */

    public static final String DIRECT_PUBLISH_EPISODE_REQUEST =
            "direct:publishEpisodeRequest";

    public static final String DIRECT_PUBLISH_EPISODE_ACTIVITY =
            "direct:publishEpisodeActivity";

    public static final String DIRECT_PUBLISH_EPISODE_PUBLICATION =
            "direct:publishEpisodePublication";

    public static final String DIRECT_FAULTY =
            "direct:faultyEndpoint";

    public static final String DIRECT_CB_PROTECTED =
            "direct:circuitBreakerProtected";

    /* ===================== RabbitMQ (JSON) ===================== */

    public static final String RABBIT_EXCHANGE_PODCAST   = "podcast.exchange";
    public static final String RABBIT_QUEUE_PODCAST      = "podcast.requests.queue";
    public static final String RABBIT_ROUTING_REQUEST    = "podcast.request";

    public static final String RABBIT_EXCHANGE_DLX       = "podcast.dlx";
    public static final String RABBIT_QUEUE_DLQ          = "podcast.dlq";
    public static final String RABBIT_ROUTING_DLQ        = "podcast.failed";

    public static final String SPRING_RABBITMQ_PRODUCER_URI =
            "spring-rabbitmq:" + RABBIT_EXCHANGE_PODCAST
                    + "?routingKey=" + RABBIT_ROUTING_REQUEST
                    + "&arg.exchange.durable=true";

    public static final String SPRING_RABBITMQ_CONSUMER_URI =
            "spring-rabbitmq:" + RABBIT_EXCHANGE_PODCAST
                    + "?queues=" + RABBIT_QUEUE_PODCAST
                    + "&routingKey=" + RABBIT_ROUTING_REQUEST
                    + "&autoDeclare=true"
                    // make Camel declare a durable queue/exchange so its declare
                    // matches what the broker may already have, and configure DLX
                    + "&arg.queue.durable=true"
                    + "&arg.exchange.durable=true"
                    + "&arg.queue.x-dead-letter-exchange=" + RABBIT_EXCHANGE_DLX
                    + "&arg.queue.x-dead-letter-routing-key=" + RABBIT_ROUTING_DLQ;
    // Note: the queue is declared with x-dead-letter-exchange / -routing-key
    // arguments by the @Bean configuration in RabbitTopologyConfig, so the
    // broker itself parks rejected messages in podcast.dlx -> podcast.dlq.

    /* ===================== Kafka topics ===================== */

    public static final String KAFKA_TOPIC_EPISODE_ACTIVITY_AVRO =
            "podcast-episode-activity-avro";

    public static final String KAFKA_TOPIC_EPISODE_PUBLICATION_PROTO =
            "podcast-episode-publication-proto";

    public static final String KAFKA_TOPIC_DLQ =
            "podcast-dlq";

    public static final String KAFKA_GROUP_EPISODE_ANALYTICS =
            "episode-analytics-group";

    public static final String KAFKA_GROUP_EPISODE_PUBLICATION =
            "episode-publication-group";

    /** Producer endpoint shared by both Avro and Protobuf flows (topic varies). */
    public static final String KAFKA_BASE_URI = "kafka:";

    /* ===================== Headers ===================== */

    public static final String HEADER_REQUEST_ID     = "requestId";
    public static final String HEADER_EPISODE_ID     = "episodeId";
    public static final String HEADER_ACTIVITY_ID    = "activityId";
    public static final String HEADER_PUBLICATION_ID = "publicationId";
    public static final String TAG_PLATFORM = "platform";
    public static final String HEADER_AUTHORIZATION  = "Authorization";
    public static final String HEADER_SOURCE         = "source";

    /* ===================== Metric names ===================== */

    public static final String METRIC_EPISODES_PROCESSED =
            "podcast.episodes.processed";

    public static final String METRIC_EPISODE_ACTIVITIES_PROCESSED =
            "podcast.episode.activities.processed";

    public static final String METRIC_EPISODE_PUBLICATIONS_PROCESSED =
            "podcast.episode.publications.processed";

    public static final String METRIC_DUPLICATES_BLOCKED =
            "podcast.duplicates.blocked";

    public static final String TAG_STATUS    = "status";
    public static final String TAG_OPERATION = "operation";
    public static final String TAG_RESULT    = "result";

    /* ===================== Idempotent repository ===================== */

    public static final String CAFFEINE_IDEMPOTENT_REPO =
            "episodePublicationIdempotentRepo";

    /* ===================== Logging messages (parameterized) ===================== */

    public static final String LOG_EPISODE_REQUEST_RECEIVED =
            "Episode request received: id=${header." + HEADER_REQUEST_ID
                    + "}, body=${body}";

    public static final String LOG_EPISODE_ACTIVITY_RECEIVED =
            "Episode activity received: id=${header." + HEADER_ACTIVITY_ID + "}";

    public static final String LOG_EPISODE_PUBLICATION_RECEIVED =
            "Episode publication event received: id=${header."
                    + HEADER_PUBLICATION_ID + "}";
}