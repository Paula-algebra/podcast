package camelpodcast.processor;

import camelpodcast.proto.EpisodePublicationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Business processor for {@link EpisodePublicationEvent} Protobuf messages.
 *
 * <p>This is where a production system would update the publication status,
 * fire downstream events (notifications/analytics), or perform a Saga
 * compensation step on failure.</p>
 */
@Component
public class PodcastPublicationProcessor  {

    private static final Logger LOG =
            LoggerFactory.getLogger(PodcastPublicationProcessor.class);

    /**
     * Processes a single episode publication.
     *
     * @param publication deserialized Protobuf episode publication
     */
    public void process(final EpisodePublicationEvent publication) {
        LOG.info("Episode publication {} for episode {} {} from show {} to platform {} -> {}",
                publication.getPublicationId(),
                publication.getEpisodeId(),
                publication.getEpisodeTitle(),
                publication.getShowName(),
                publication.getPlatform(),
                publication.getStatus());
    }
}