package camelpodcast.processor;

import camelpodcast.avro.EpisodeActivity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Business processor for incoming {@link EpisodeActivity} Avro messages
 * received from Kafka. Rejects unknown activity operations; in a real pipeline
 * these would be forwarded to an analytics store.
 */
@Component
public class EpisodeActivityProcessor {

    private static final Logger LOG =
            LoggerFactory.getLogger(EpisodeActivityProcessor.class);

    private static final Set<String> VALID_OPERATIONS =
            Set.of("VIEWED", "CREATED", "UPDATED", "DELETED");

    /**
     * Processes an episode activity.
     *
     * @param activity deserialized Avro record
     */
    public void process(final EpisodeActivity activity) {
        LOG.info("Episode [{}] @ {} -> operation={}, title={}",
                activity.getEpisodeId(),
                activity.getTimestamp(),
                activity.getOperation(),
                activity.getTitle());

        if (!VALID_OPERATIONS.contains(activity.getOperation())) {
            LOG.warn("Suspicious episode activity: {} for episode {}",
                    activity.getOperation(),
                    activity.getEpisodeId());
        }
    }
}