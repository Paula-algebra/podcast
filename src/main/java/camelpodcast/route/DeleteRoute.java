package camelpodcast.route;

import camelpodcast.config.AppConfig;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import camelpodcast.processor.ResponseProcessor;
import org.springframework.beans.factory.annotation.Autowired;

@Component
public class DeleteRoute extends RouteBuilder {

    private final ObjectMapper mapper = new ObjectMapper();
    @Autowired
    private ResponseProcessor responseProcessor;

    private final String payload = """
            {
              "title": "Camel Delete Test Episode",
              "showName": "Camel Delete Show",
              "hosts": "Camel Host",
              "category": "COMEDY",
              "status": "QUEUED",
              "rating": 2,
              "guests": "Delete Guest",
              "network": "Spotify",
              "episodeNumber": "D1",
              "seasonNumber": 1,
              "listeningContext": "COMMUTE",
              "playbackSpeed": "SPEED_0_75X",
              "durationMinutes": 30,
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
              "moodTags": "Camel delete",
              "mainTopic": "Delete test",
              "memorableQuote": "Delete quote",
              "keyTakeaway": "Delete takeaway",
              "review": "Created only to test delete",
              "personalNotes": "Camel delete test"
            }
            """;

    @Override
    public void configure() {
        onException(Exception.class)
                .handled(true)
                .log("Delete episode failed: ${exception.message}")
                .setBody(simple("{\"error\":\"${exception.message}\"}"))
                .toD("file:" + AppConfig.OUTPUT_DIR + "?fileName=delete-episode-error-${date:now:yyyyMMdd-HHmmss}.json");

        from("timer:deleteEpisodeTimer?period=60000")
                .routeId("delete-episode")
                .log("Starting delete episode route")

                .setBody(constant(payload))
                .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .setHeader("Authorization", constant("Bearer " + AppConfig.ACCESS_TOKEN))
                .to(AppConfig.BASE_URL + "/api/episodes")

                .process(exchange -> {
                    String createdJson = exchange.getMessage().getBody(String.class);
                    JsonNode node = mapper.readTree(createdJson);
                    long createdId = node.get("id").asLong();

                    exchange.setProperty("deletedId", createdId);
                    exchange.getMessage().setHeader(Exchange.HTTP_PATH, "/api/episodes/" + createdId);
                    exchange.getMessage().setBody(null);
                })

                .setHeader(Exchange.HTTP_METHOD, constant("DELETE"))
                .setHeader("Authorization", constant("Bearer " + AppConfig.ACCESS_TOKEN))
                .to(AppConfig.BASE_URL)

                .process(exchange -> {
                    Object deletedId = exchange.getProperty("deletedId");
                    Object status = exchange.getMessage().getHeader(Exchange.HTTP_RESPONSE_CODE);

                    exchange.setProperty("endpoint", AppConfig.BASE_URL + "/api/episodes/" + deletedId);
                    exchange.getMessage().setHeader(Exchange.HTTP_METHOD, "DELETE");
                    exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, status);

                    String summary = """
            {
              "operation": "delete",
              "createdThenDeletedId": "%s",
              "httpStatus": "%s",
              "message": "Episode was created and then deleted by Camel route"
            }
            """.formatted(deletedId, status);

                    exchange.getMessage().setBody(summary);
                })
                .process(responseProcessor)
                .log("Delete episode completed for ID ${exchangeProperty.deletedId} with status ${header.CamelHttpResponseCode}")
                .toD("file:" + AppConfig.OUTPUT_DIR + "?fileName=delete-episode-id-${exchangeProperty.deletedId}-${date:now:yyyyMMdd-HHmmss}.json");
    }
}