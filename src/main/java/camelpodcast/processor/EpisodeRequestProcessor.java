package camelpodcast.processor;

import camelpodcast.config.AppConfig;
import camelpodcast.model.PodcastRequestEvent;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

/**
 * Business processor for incoming {@link PodcastRequestEvent} messages received
 * from RabbitMQ. In a real system this is where you would persist the episode
 * request to the database, push it to the Podcast REST API, etc.
 */
@Component
public class EpisodeRequestProcessor implements Processor {

    @Override
    public void process(final Exchange exchange) {
        final PodcastRequestEvent request =
                exchange.getMessage().getBody(PodcastRequestEvent.class);

        exchange.setProperty(
                "endpoint",
                AppConfig.BASE_URL + request.endpoint()
        );

        exchange.getMessage().setHeader(
                Exchange.HTTP_METHOD,
                request.httpMethod()
        );

        exchange.getMessage().setHeader(
                "Authorization",
                "Bearer " + AppConfig.ACCESS_TOKEN
        );

        exchange.getMessage().setHeader(
                Exchange.CONTENT_TYPE,
                "application/json"
        );

        exchange.getMessage().setBody(request.requestBody());
    }
}