package camelpodcast.service;

import camelpodcast.avro.EpisodeActivity;
import camelpodcast.config.ApplicationConstants;
import camelpodcast.model.PodcastRequestEvent;
import camelpodcast.proto.EpisodePublicationEvent;
import camelpodcast.proto.PublicationStatus;
import org.apache.camel.ProducerTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Helper service that builds sample messages and pushes them into the demo
 * routes via Camel's {@link ProducerTemplate}. Used both by the REST
 * controller and (optionally) by an automated traffic generator.
 */
@Service
public class EventGeneratorService {

    private static final String[] SHOWS = {
            "Camel task show", "Algebra podcasts", "RabbitMQ story"
    };

    private static final String[] PLATFORMS = {
            "Spotify", "Apple Podcasts", "YouTube Music"
    };

    private static final String[] OPERATIONS = {
            "VIEWED", "CREATED", "UPDATED", "DELETED"
    };

    private final ProducerTemplate producerTemplate;

    public EventGeneratorService(final ProducerTemplate producerTemplate) {
        this.producerTemplate = producerTemplate;
    }

    /**
     * Generates a single random {@link PodcastRequestEvent} (JSON over
     * RabbitMQ).
     *
     * @return the sent episode request
     */
    public PodcastRequestEvent buildRandomEpisodeRequest() {
        final int number = ThreadLocalRandom.current().nextInt(1, 10_000);

        final String requestBody = """
            {
              "title": "Camel Created Episode %d",
              "showName": "Camel Show",
              "hosts": "Camel Host",
              "category": "COMEDY",
              "status": "QUEUED",
              "rating": 5,
              "guests": "Guest A",
              "network": "Spotify",
              "episodeNumber": "C%d",
              "seasonNumber": 1,
              "listeningContext": "COMMUTE",
              "playbackSpeed": "SPEED_1_0X",
              "durationMinutes": 45,
              "minutesListened": 0,
              "contentQuality": 5,
              "audioQuality": 5,
              "hostChemistry": 5,
              "rewatchValue": 5,
              "explicitContent": false,
              "subscribed": false,
              "bookmarkedQuote": false,
              "recommendToFriend": false,
              "releaseDate": "2026-06-28",
              "listenedDate": "2026-06-28",
              "addedDate": "2026-06-28",
              "moodTags": "Camel test",
              "mainTopic": "Camel integration",
              "memorableQuote": "Test quote",
              "keyTakeaway": "Test takeaway",
              "review": "Created from Camel route",
              "personalNotes": "Camel generated test"
            }
            """.formatted(number, number);

        return PodcastRequestEvent.newRequest(
                "POST",
                "/api/episodes",
                requestBody);
    }

    public PodcastRequestEvent sendRandomEpisodeRequest() {
        PodcastRequestEvent request = buildRandomEpisodeRequest();

        producerTemplate.sendBody(
                ApplicationConstants.DIRECT_PUBLISH_EPISODE_REQUEST,
                request);

        return request;
    }

    /**
     * Generates and publishes a single random {@link EpisodeActivity}
     * (Avro over Kafka).
     *
     * @return the sent activity
     */
    public EpisodeActivity sendRandomEpisodeActivity() {
        final long episodeId =
                ThreadLocalRandom.current().nextLong(1L, 100L);

        final EpisodeActivity activity = EpisodeActivity.newBuilder()
                .setEventId(UUID.randomUUID().toString())
                .setOperation(OPERATIONS[
                        ThreadLocalRandom.current().nextInt(OPERATIONS.length)])
                .setEpisodeId(episodeId)
                .setTitle("Episode " + episodeId)
                .setTimestamp(Instant.now())
                .build();

        producerTemplate.sendBody(
                ApplicationConstants.DIRECT_PUBLISH_EPISODE_ACTIVITY,
                activity);

        return activity;
    }

    /**
     * Generates and publishes a single random {@link EpisodePublicationEvent}
     * (Protobuf over Kafka).
     *
     * @return the sent publication
     */
    public EpisodePublicationEvent sendRandomEpisodePublication() {
        final long episodeId =
                ThreadLocalRandom.current().nextLong(1L, 100L);

        final EpisodePublicationEvent publication =
                EpisodePublicationEvent.newBuilder()
                        .setPublicationId(UUID.randomUUID().toString())
                        .setEpisodeId(String.valueOf(episodeId))
                        .setEpisodeTitle("Episode " + episodeId)
                        .setShowName(SHOWS[
                                ThreadLocalRandom.current().nextInt(SHOWS.length)])
                        .setPlatform(PLATFORMS[
                                ThreadLocalRandom.current()
                                        .nextInt(PLATFORMS.length)])
                        .setTimestamp(Instant.now().toEpochMilli())
                        .setStatus(PublicationStatus.PENDING)
                        .build();

        producerTemplate.sendBody(
                ApplicationConstants.DIRECT_PUBLISH_EPISODE_PUBLICATION,
                publication);

        return publication;
    }

    /**
     * Sends the SAME episode publication twice to demonstrate the Idempotent Consumer EIP.
     *
     * @return the duplicated episode publication
     */
    public EpisodePublicationEvent sendDuplicateEpisodePublication() {
        final EpisodePublicationEvent publication =
                EpisodePublicationEvent.newBuilder()
                        .setPublicationId(UUID.randomUUID().toString())
                        .setEpisodeId("19")
                        .setEpisodeTitle("Camel Episode")
                        .setShowName("Camel Show")
                        .setPlatform("Spotify")
                        .setTimestamp(Instant.now().toEpochMilli())
                        .setStatus(PublicationStatus.PENDING)
                        .build();

        // Same publicationId twice -> the second one MUST be filtered out.
        producerTemplate.sendBody(
                ApplicationConstants.DIRECT_PUBLISH_EPISODE_PUBLICATION,
                publication);
        producerTemplate.sendBody(
                ApplicationConstants.DIRECT_PUBLISH_EPISODE_PUBLICATION,
                publication);

        return publication;
    }
}